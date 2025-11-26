package dataaccess;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Asset;
import entity.Stock;
import entity.SubAccount;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import usecase.SubAccount.SubAccountDataAccessInterface;
import usecase.exchange.ExchangeDataAccessInterface;
import usecase.sell_asset.SellAssetDataAccessInterface;
import usecase.transfer.TransferDataAccessInterface;

public class FileSubAccountDataAccessJSON implements
    SubAccountDataAccessInterface,
    TransferDataAccessInterface,
    SellAssetDataAccessInterface,
    ExchangeDataAccessInterface {
    private final Path filePath;
    private final Map<String, List<SubAccount>> data = new HashMap<>();

    public FileSubAccountDataAccessJSON(final String filename) {
        this.filePath = Paths.get(filename);
        loadFromFile();
    }

    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            return;
        }
        try {
            final String content = Files.readString(filePath, StandardCharsets.UTF_8);
            if (content.isBlank()) {
                return;
            }
            final JSONObject root = new JSONObject(content);
            for (final String username : root.keySet()) {
                final JSONArray saArray = root.getJSONArray(username);
                final List<SubAccount> list = data.computeIfAbsent(username, u -> new ArrayList<>());
                for (int i = 0; i < saArray.length(); i++) {
                    final JSONObject saJson = saArray.getJSONObject(i);
                    final String name = saJson.getString("name");
                    final String balStr = saJson.optString("balanceUSD", "0");
                    final boolean undeletable = saJson.optBoolean("undeletable", false);
                    BigDecimal balanceUSD;
                    try {
                        balanceUSD = new BigDecimal(balStr);
                    }
                    catch (final NumberFormatException e) {
                        balanceUSD = BigDecimal.ZERO;
                    }
                    final SubAccount sa = new SubAccount(name, balanceUSD, undeletable);

                    if (saJson.has("currencies")) {
                        final JSONObject curObj = saJson.getJSONObject("currencies");
                        for (final String code : curObj.keySet()) {
                            final String amtStr = curObj.get(code).toString();
                            try {
                                final BigDecimal amt = new BigDecimal(amtStr);
                                sa.setBalanceOf(code, amt);
                            }
                            catch (final NumberFormatException ignored) {
                            }
                        }
                    }
                    if (saJson.has("Stock")) {
                        final JSONArray stockArray = saJson.getJSONArray("Stock");
                        for (int j = 0; j < stockArray.length(); j++) {
                            final JSONObject sJson = stockArray.getJSONObject(j);
                            final String symbol = sJson.getString("symbol");
                            final double quantity = sJson.getDouble("quantity");
                            final Stock stock = new Stock(symbol, quantity, symbol);
                            sa.addOrIncreaseAsset(stock);
                        }
                    }
                    list.remove(sa);
                    list.add(sa);
                }
            }

        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void saveToFile() {
        try {
            final JSONObject root = new JSONObject();
            for (final Map.Entry<String, List<SubAccount>> entry : data.entrySet()) {
                final String username = entry.getKey();
                final JSONArray saArray = new JSONArray();
                for (final SubAccount sa : entry.getValue()) {
                    final JSONObject saJson = new JSONObject();
                    saJson.put("name", sa.getName());
                    saJson.put("balanceUSD", sa.getBalanceUSD().toString());
                    saJson.put("undeletable", sa.isUndeletable());

                    final JSONObject curObj = new JSONObject();
                    for (final Map.Entry<String, BigDecimal> ce : sa.getCurrencies().entrySet()) {
                        curObj.put(ce.getKey(), ce.getValue().toString());
                    }
                    saJson.put("currencies", curObj);

                    final JSONArray stockArray = new JSONArray();
                    for (final Asset a : sa.getAssets()) {
                        if (a instanceof final Stock s) {
                            final JSONObject sJson = new JSONObject();
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
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean exists(final String username, final String subName) {
        return data.getOrDefault(username, List.of()).stream()
            .anyMatch(sa -> sa.getName().equalsIgnoreCase(subName));
    }

    @Override
    public void save(final String username, final SubAccount subAccount) {
        final List<SubAccount> list = data.computeIfAbsent(username, u -> new ArrayList<>());
        list.remove(subAccount);
        list.add(subAccount);
        saveToFile();
    }

    @Override
    public List<SubAccount> getSubAccountsOf(final String username) {
        return new ArrayList<>(data.getOrDefault(username, List.of()));
    }

    @Override
    public int countByUser(final String username) {
        return data.getOrDefault(username, List.of()).size();
    }

    @Override
    public void delete(final String username, final String subName) {
        final List<SubAccount> list = data.get(username);
        if (list != null) {
            list.removeIf(sa -> sa.getName().equalsIgnoreCase(subName));
            saveToFile();
        }
    }

    @Override
    public boolean portfolioExists(final String username, final String portfolioId) {
        return exists(username, portfolioId);
    }

    @Override
    public boolean hasAsset(final String username, final String portfolioId, final String assetSymbol) {
        final List<SubAccount> accounts = data.get(username);
        if (accounts == null) {
            return false;
        }

        for (final SubAccount sa : accounts) {
            if (sa.getName().equals(portfolioId)) {
                // Check currency first
                if (sa.getCurrencies().containsKey(assetSymbol)) {
                    return true;
                }
                // Check stocks
                for (final Asset asset : sa.getAssets()) {
                    if (asset instanceof Stock && ((Stock) asset).getCompanySymbol().equalsIgnoreCase(assetSymbol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public double getAssetBalance(final String username, final String portfolioId, final String assetSymbol) {
        final List<SubAccount> accounts = data.get(username);
        if (accounts == null) {
            return 0.0;
        }

        for (final SubAccount sa : accounts) {
            if (sa.getName().equalsIgnoreCase(portfolioId)) {
                // Check currency
                if (sa.getCurrencies().containsKey(assetSymbol)) {
                    return sa.getBalanceOf(assetSymbol).doubleValue();
                }
                // Check stocks
                for (final Asset asset : sa.getAssets()) {
                    if (asset instanceof Stock && ((Stock) asset).getCompanySymbol().equalsIgnoreCase(assetSymbol)) {
                        return asset.getQuantity();
                    }
                }
            }
        }
        return 0.0;
    }

    @Override
    public void transferAsset(final String username, final String fromPortfolio,
                              final String toPortfolio, final String assetSymbol, final double amount) {
        final List<SubAccount> accounts = data.get(username);
        if (accounts == null) {
            throw new IllegalArgumentException("User not found.");
        }

        SubAccount from = null;
        SubAccount to = null;
        for (final SubAccount sa : accounts) {
            if (sa.getName().equals(fromPortfolio)) {
                from = sa;
            }
            if (sa.getName().equals(toPortfolio)) {
                to = sa;
            }
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException("Portfolio not found.");
        }

        // 1. Check if it is a Stock transfer
        Stock sourceStock = null;
        for (final Asset asset : from.getAssets()) {
            if (asset instanceof final Stock s) {
                if (s.getCompanySymbol().equalsIgnoreCase(assetSymbol)) {
                    sourceStock = s;
                    break;
                }
            }
        }

        if (sourceStock != null) {
            // Transferring Stock
            if (sourceStock.getQuantity() < amount) {
                throw new IllegalArgumentException("Insufficient stock quantity.");
            }

            // Update Sender
            sourceStock.setQuantity(sourceStock.getQuantity() - amount);
            if (sourceStock.getQuantity() == 0) {
                from.removeAsset(sourceStock);
            }

            // Update Receiver (SubAccount logic handles existing stock check automatically)
            final Stock newStock = new Stock(assetSymbol, amount, assetSymbol);
            to.addOrIncreaseAsset(newStock);

        }
        else {
            // 2. Fallback to Currency transfer
            final BigDecimal amt = BigDecimal.valueOf(amount);
            final BigDecimal fromBalance = from.getBalanceOf(assetSymbol);

            if (fromBalance.compareTo(amt) < 0) {
                throw new IllegalArgumentException("Insufficient funds.");
            }

            // Subtract from sender
            from.setBalanceOf(assetSymbol, fromBalance.subtract(amt));

            // Add to receiver
            final BigDecimal toBalance = to.getBalanceOf(assetSymbol);
            to.setBalanceOf(assetSymbol, toBalance.add(amt));
        }

        saveToFile();
    }

    @Override
    public String[] getAvailablePortfolios(final String username) {
        final List<SubAccount> accounts = getSubAccountsOf(username);
        final String[] names = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            names[i] = accounts.get(i).getName();
        }
        return names;
    }

    @Override
    public String[] getAvailableStocks(final String username, final String portfolioId) {
        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount sa : accounts) {
                if (sa.getName().equals(portfolioId)) {
                    final List<String> symbols = new ArrayList<>();
                    for (final Asset a : sa.getAssets()) {
                        if (a instanceof Stock) {
                            symbols.add(((Stock) a).getCompanySymbol());
                        }
                    }
                    return symbols.toArray(new String[0]);
                }
            }
        }
        return new String[0];
    }

    @Override
    public double getStockQuantity(final String username, final String portfolioName, final String stockName) {
        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount sa : accounts) {
                if (sa.getName().equals(portfolioName)) {
                    for (final Asset asset : sa.getAssets()) {
                        if (asset instanceof final Stock stock) {
                            if (stock.getCompanySymbol().equals(stockName)) {
                                return stock.getQuantity();
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public void updateStockQuantity(final String username, final String portfolioName, final String stockName,
                                    final double quantity) {
        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount sa : accounts) {
                if (sa.getName().equals(portfolioName)) {
                    for (final Asset asset : sa.getAssets()) {
                        if (asset instanceof final Stock stock) {
                            if (stock.getCompanySymbol().equals(stockName)) {
                                stock.setQuantity(quantity);
                                saveToFile();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void removeStock(final String username, final String portfolioName, final String stockName) {
        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount sa : accounts) {
                if (sa.getName().equals(portfolioName)) {
                    final Iterator<Asset> iter = sa.getAssets().iterator();
                    while (iter.hasNext()) {
                        final Asset asset = iter.next();
                        if (asset instanceof final Stock stock) {
                            if (stock.getCompanySymbol().equals(stockName)) {
                                sa.removeAsset(stock);
                                saveToFile();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addCashToPortfolio(final String username, final String portfolioName, final double amount) {
        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount sa : accounts) {
                if (sa.getName().equals(portfolioName)) {
                    // Get existing USD balance
                    BigDecimal current = sa.getBalanceOf("USD");
                    if (current == null) {
                        current = BigDecimal.ZERO;
                    }

                    // Add the amount
                    final BigDecimal updated = current.add(BigDecimal.valueOf(amount));
                    sa.setBalanceOf("USD", updated);

                    // Persist changes
                    saveToFile();
                    return;
                }
            }
        }
    }

    @Override
    public String[] getAvailableCurrencies(final String username, final String portfolioId) {
        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount sa : accounts) {
                if (sa.getName().equals(portfolioId)) {
                    final Set<String> keys = sa.getCurrencies().keySet();
                    return keys.toArray(new String[0]);
                }
            }
        }
        return new String[] {"USD"};
    }

    @Override
    public Map<String, Double> getCurrencies(final String username, final String accountName) {
        final List<SubAccount> list = data.get(username);
        if (list == null) {
            throw new RuntimeException("User not found: " + username);
        }

        for (final SubAccount sa : list) {
            if (sa.getName().equals(accountName)) {
                final Map<String, Double> map = new HashMap<>();
                for (final Map.Entry<String, BigDecimal> e : sa.getCurrencies().entrySet()) {
                    map.put(e.getKey(), e.getValue().doubleValue());
                }
                return map;
            }
        }
        throw new RuntimeException("Account not found: " + accountName);
    }

    @Override
    public void saveCurrencies(final String username, final String accountName, final Map<String, Double> currencies) {
        final List<SubAccount> list = data.get(username);
        if (list == null) {
            throw new RuntimeException("User not found: " + username);
        }

        for (final SubAccount sa : list) {
            if (sa.getName().equals(accountName)) {
                for (final Map.Entry<String, Double> e : currencies.entrySet()) {
                    sa.setBalanceOf(e.getKey(), BigDecimal.valueOf(e.getValue()));
                }
                saveToFile();
                return;
            }
        }
        throw new RuntimeException("Account not found: " + accountName);
    }

    public Map<String, Double> getRates(final String currency) {

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
            .url("https://open.er-api.com/v6/latest/" + currency)
            .get()
            .build();

        final HashMap<String, Double> rates = new HashMap<>();

        try (final Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("API response error");
            }

            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getString("result").equals("success")) {
                final JSONObject rateObject = responseBody.getJSONObject("rates");
                final Iterator<String> keys = rateObject.keys();

                while (keys.hasNext()) {
                    final String key = keys.next();
                    rates.put(key, rateObject.getDouble(key));
                }
            }
            else {
                throw new RuntimeException("API returned failure");
            }

        }
        catch (final IOException | JSONException e) {
            throw new RuntimeException("Failed to fetch exchange rates", e);
        }

        return rates;
    }
}
