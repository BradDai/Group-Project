package data_access;

import entity.Asset;
import entity.Stock;
import entity.SubAccount;
import use_case.SubAccount.SubAccountDataAccessInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileSubAccountDataAccessJSON implements SubAccountDataAccessInterface {
    private final Path filePath;
    private final Map<String, List<SubAccount>> data = new HashMap<>();
    public FileSubAccountDataAccessJSON(String filename) {
        this.filePath = Paths.get(filename);
        loadFromFile();
    }
    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            return;
        }
        try {
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            if (content.isBlank()) {
                return;
            }
            JSONObject root = new JSONObject(content);
            for (String username : root.keySet()) {
                JSONArray saArray = root.getJSONArray(username);
                List<SubAccount> list = data.computeIfAbsent(username, u -> new ArrayList<>());
                for (int i = 0; i < saArray.length(); i++) {
                    JSONObject saJson = saArray.getJSONObject(i);
                    String name = saJson.getString("name");
                    String balStr = saJson.optString("balanceUSD", "0");
                    boolean undeletable = saJson.optBoolean("undeletable", false);
                    BigDecimal balanceUSD;
                    try {
                        balanceUSD = new BigDecimal(balStr);
                    } catch (NumberFormatException e) {
                        balanceUSD = BigDecimal.ZERO;
                    }
                    SubAccount sa = new SubAccount(name, balanceUSD, undeletable);
                    if (saJson.has("currencies")) {
                        JSONObject curObj = saJson.getJSONObject("currencies");
                        for (String code : curObj.keySet()) {
                            String amtStr = curObj.get(code).toString();
                            try {
                                BigDecimal amt = new BigDecimal(amtStr);
                                sa.setBalanceOf(code, amt);
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                    if (saJson.has("Stock")) {
                        JSONArray stockArray = saJson.getJSONArray("Stock");
                        for (int j = 0; j < stockArray.length(); j++) {
                            JSONObject sJson = stockArray.getJSONObject(j);
                            String symbol = sJson.getString("symbol");
                            double quantity = sJson.getDouble("quantity");
                            Stock stock = new Stock(symbol, quantity, symbol);
                            sa.addOrIncreaseAsset(stock);
                        }
                    }
                    list.remove(sa);
                    list.add(sa);
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    private void saveToFile() {
        try {
            JSONObject root = new JSONObject();

            for (Map.Entry<String, List<SubAccount>> entry : data.entrySet()) {
                String username = entry.getKey();
                JSONArray saArray = new JSONArray();

                for (SubAccount sa : entry.getValue()) {
                    JSONObject saJson = new JSONObject();
                    saJson.put("name", sa.getName());
                    saJson.put("balanceUSD", sa.getBalanceUSD().toString());
                    saJson.put("undeletable", sa.isUndeletable());

                    // ---- 写 currencies ----
                    JSONObject curObj = new JSONObject();
                    for (Map.Entry<String, BigDecimal> ce : sa.getCurrencies().entrySet()) {
                        curObj.put(ce.getKey(), ce.getValue().toString());
                    }
                    saJson.put("currencies", curObj);

                    // ---- 写 Stock 数组（只把股票挑出来）----
                    JSONArray stockArray = new JSONArray();
                    for (Asset a : sa.getAssets()) {
                        if (a instanceof Stock) {
                            Stock s = (Stock) a;
                            JSONObject sJson = new JSONObject();
                            sJson.put("symbol", s.getCompanySymbol());
                            sJson.put("quantity", s.getQuantity());
                            stockArray.put(sJson);
                        }
                    }
                    saJson.put("Stock", stockArray);

                    saArray.put(saJson);
                }

                root.put(username, saArray);
            }

            Files.writeString(filePath, root.toString(2), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    @Override
    public boolean exists(String username, String subName) {
        return data.getOrDefault(username, List.of())
                .stream()
                .anyMatch(sa -> sa.getName().equalsIgnoreCase(subName));
    }
    @Override
    public void save(String username, SubAccount subAccount) {
        List<SubAccount> list = data.computeIfAbsent(username, u -> new ArrayList<>());
        list.remove(subAccount);
        list.add(subAccount);
        saveToFile();
    }

    @Override
    public List<SubAccount> getSubAccountsOf(String username) {
        return new ArrayList<>(data.getOrDefault(username, List.of()));
    }

    @Override
    public int countByUser(String username) {
        return data.getOrDefault(username, List.of()).size();
    }

    @Override
    public void delete(String username, String subName) {
        List<SubAccount> list = data.get(username);
        if (list == null) {
            return;
        }
        list.removeIf(sa -> sa.getName().equalsIgnoreCase(subName));
        saveToFile();
    }
}