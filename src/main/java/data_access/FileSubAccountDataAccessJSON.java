package data_access;

import entity.Asset;
import entity.Stock;
import entity.SubAccount;
import entity.transaction.Transaction;
import use_case.SubAccount.SubAccountDataAccessInterface;
import use_case.transfer.TransferDataAccessInterface;
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

public class FileSubAccountDataAccessJSON implements SubAccountDataAccessInterface, TransferDataAccessInterface {
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
                            } catch (NumberFormatException e) {}
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

                    JSONObject curObj = new JSONObject();
                    for (Map.Entry<String, BigDecimal> ce : sa.getCurrencies().entrySet()) {
                        curObj.put(ce.getKey(), ce.getValue().toString());
                    }
                    saJson.put("currencies", curObj);

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
        return data.getOrDefault(username, List.of()).stream()
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
        if (list != null) {
            list.removeIf(sa -> sa.getName().equalsIgnoreCase(subName));
            saveToFile();
        }
    }

    @Override
    public boolean portfolioExists(String username, String portfolioId) {
        return exists(username, portfolioId);
    }

    @Override
    public boolean hasAsset(String username, String portfolioId, String assetSymbol) {
        return true;
    }

    @Override
    public double getAssetBalance(String username, String portfolioId, String assetSymbol) {
        List<SubAccount> accounts = data.get(username);
        if (accounts == null) return 0.0;

        for (SubAccount sa : accounts) {
            if (sa.getName().equalsIgnoreCase(portfolioId)) {
                if ("USD".equalsIgnoreCase(assetSymbol)) {
                    return sa.getBalanceUSD().doubleValue();
                } else {
                    return sa.getBalanceOf(assetSymbol).doubleValue();
                }
            }
        }
        return 0.0;
    }

    @Override
    public void transferAsset(String username, String fromPortfolio, String toPortfolio, String assetSymbol, double amount) {
        List<SubAccount> accounts = data.get(username);
        if (accounts == null) throw new IllegalArgumentException("User not found.");

        SubAccount from = null, to = null;
        for (SubAccount sa : accounts) {
            if (sa.getName().equals(fromPortfolio)) from = sa;
            if (sa.getName().equals(toPortfolio)) to = sa;
        }

        if (from == null || to == null) throw new IllegalArgumentException("Portfolio not found.");

        if ("USD".equalsIgnoreCase(assetSymbol)) {
            BigDecimal amt = BigDecimal.valueOf(amount);
            if (from.getBalanceUSD().compareTo(amt) < 0) {
                throw new IllegalArgumentException("Insufficient funds.");
            }
            from.setBalanceUSD(from.getBalanceUSD().subtract(amt));
            to.setBalanceUSD(to.getBalanceUSD().add(amt));
            saveToFile();
        } else {
            throw new UnsupportedOperationException("Only USD transfers are currently supported in JSON.");
        }
    }

    @Override
    public void saveTransaction(Transaction transaction) {
    }

    @Override
    public String[] getAvailablePortfolios(String username) {
        List<SubAccount> accounts = getSubAccountsOf(username);
        System.out.println("Fetching portfolios for user [" + username + "]. Found: " + accounts.size());
        String[] names = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            names[i] = accounts.get(i).getName();
        }
        return names;
    }

    @Override
    public String[] getAvailableStocks(String username, String portfolioId) {
        return new String[0];
    }

    @Override
    public String[] getAvailableCurrencies(String username, String portfolioId) {
        return new String[]{"USD"};
    }

    @Override
    public double getStockPrice(String symbol) {
        return 0.0;
    }
}