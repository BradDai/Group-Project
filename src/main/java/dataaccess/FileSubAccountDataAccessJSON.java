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

    private static final String KEY_USD = "USD";
    private static final String KEY_CURRENCIES = "currencies";
    private static final String KEY_STOCKS = "Stock";

    private final Path filePath;
    private final Map<String, List<SubAccount>> data = new HashMap<>();

    public FileSubAccountDataAccessJSON(final String filename) {
        this.filePath = Paths.get(filename);
        loadFromFile();
    }

    private void loadFromFile() {
        if (Files.exists(filePath)) {
            try {
                final String content =
                        Files.readString(filePath, StandardCharsets.UTF_8);
                if (!content.isBlank()) {
                    parseJsonContent(content);
                }
            }
            catch (final IOException evt) {
                throw new UncheckedIOException(evt);
            }
        }
    }

    private void parseJsonContent(final String content) {
        final JSONObject root = new JSONObject(content);
        for (final String username : root.keySet()) {
            final JSONArray subAccountArray = root.getJSONArray(username);
            parseUserSubAccounts(username, subAccountArray);
        }
    }

    private void parseUserSubAccounts(final String username,
                                      final JSONArray subAccountArray) {

        final List<SubAccount> list = data.computeIfAbsent(
                username,
                userKey -> new ArrayList<>());

        for (int i = 0; i < subAccountArray.length(); i++) {
            final JSONObject subAccountJson =
                    subAccountArray.getJSONObject(i);
            final SubAccount subAccount = parseSubAccount(subAccountJson);
            list.remove(subAccount);
            list.add(subAccount);
        }
    }

    private SubAccount parseSubAccount(final JSONObject subAccountJson) {
        final String name = subAccountJson.getString("name");
        final String balanceString =
                subAccountJson.optString("balanceUsd", "0");
        final boolean undeletable =
                subAccountJson.optBoolean("undeletable", false);

        final BigDecimal balanceUsd = parseBigDecimalOrZero(balanceString);

        final SubAccount subAccount =
                new SubAccount(name, balanceUsd, undeletable);

        loadCurrencies(subAccountJson, subAccount);
        loadStocks(subAccountJson, subAccount);

        return subAccount;
    }

    private BigDecimal parseBigDecimalOrZero(final String value) {
        BigDecimal result;
        try {
            result = new BigDecimal(value);
        }
        catch (final NumberFormatException evt) {
            result = BigDecimal.ZERO;
        }
        return result;
    }

    private void loadCurrencies(final JSONObject subAccountJson,
                                final SubAccount subAccount) {

        if (subAccountJson.has(KEY_CURRENCIES)) {
            final JSONObject currenciesJson =
                    subAccountJson.getJSONObject(KEY_CURRENCIES);

            for (final String code : currenciesJson.keySet()) {
                final String amountString =
                        currenciesJson.get(code).toString();
                try {
                    final BigDecimal amount =
                            new BigDecimal(amountString);
                    subAccount.setBalanceOf(code, amount);
                }
                catch (final NumberFormatException ignored) {
                    // ignore invalid currency
                }
            }
        }
    }

    private void loadStocks(final JSONObject subAccountJson,
                            final SubAccount subAccount) {

        if (subAccountJson.has(KEY_STOCKS)) {
            final JSONArray stockArray =
                    subAccountJson.getJSONArray(KEY_STOCKS);

            for (int i = 0; i < stockArray.length(); i++) {
                final JSONObject stockJson = stockArray.getJSONObject(i);
                final String symbol = stockJson.getString("symbol");
                final double quantity = stockJson.getDouble("quantity");
                final Stock stock = new Stock(symbol, quantity, symbol);
                subAccount.addOrIncreaseAsset(stock);
            }
        }
    }

    private void saveToFile() {
        try {
            final JSONObject root = new JSONObject();

            for (final Map.Entry<String, List<SubAccount>> entry
                    : data.entrySet()) {

                final String username = entry.getKey();
                final JSONArray saArray = new JSONArray();

                for (final SubAccount sa : entry.getValue()) {
                    final JSONObject saJson = new JSONObject();
                    saJson.put("name", sa.getName());
                    saJson.put("balanceUSD",
                            sa.getBalanceUSD().toString());
                    saJson.put("undeletable", sa.isUndeletable());

                    final JSONObject curObj = new JSONObject();
                    for (final Map.Entry<String, BigDecimal> ce
                            : sa.getCurrencies().entrySet()) {
                        curObj.put(ce.getKey(),
                                ce.getValue().toString());
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

            Files.writeString(
                    filePath,
                    root.toString(2),
                    StandardCharsets.UTF_8);
        }
        catch (final IOException evt) {
            throw new UncheckedIOException(evt);
        }
    }

    @Override
    public boolean exists(final String username, final String subName) {
        return data.getOrDefault(username, List.of()).stream()
                .anyMatch(sub -> {
                    return sub.getName().equalsIgnoreCase(subName);
                });
    }

    @Override
    public void save(final String username,
                     final SubAccount subAccount) {

        final List<SubAccount> list =
                data.computeIfAbsent(
                        username,
                        uuu -> new ArrayList<>());
        list.remove(subAccount);
        list.add(subAccount);
        saveToFile();
    }

    @Override
    public List<SubAccount> getSubAccountsOf(final String username) {
        return new ArrayList<>(
                data.getOrDefault(username, List.of()));
    }

    @Override
    public int countByUser(final String username) {
        return data.getOrDefault(username, List.of()).size();
    }

    @Override
    public void delete(final String username, final String subName) {
        final List<SubAccount> list = data.get(username);
        if (list != null) {
            list.removeIf(sub -> {
                return sub.getName().equalsIgnoreCase(subName);
            });
            saveToFile();
        }
    }

    @Override
    public boolean portfolioExists(final String username,
                                   final String portfolioId) {
        return exists(username, portfolioId);
    }

    @Override
    public boolean hasAsset(final String username,
                            final String portfolioId,
                            final String assetSymbol) {

        boolean found = false;

        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount subAccount : accounts) {
                if (subAccount.getName().equals(portfolioId)) {

                    // Check currencies
                    if (subAccount.getCurrencies()
                            .containsKey(assetSymbol)) {
                        found = true;
                        break;
                    }

                    // Check stocks
                    for (final Asset asset
                            : subAccount.getAssets()) {
                        if (asset instanceof Stock
                                && ((Stock) asset)
                                .getCompanySymbol()
                                .equalsIgnoreCase(assetSymbol)) {
                            found = true;
                            break;
                        }
                    }
                }

                if (found) {
                    break;
                }
            }
        }

        return found;
    }

    @Override
    public double getAssetBalance(final String username,
                                  final String portfolioId,
                                  final String assetSymbol) {

        double balance = 0.0;
        boolean found = false;

        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount subAccount : accounts) {
                if (subAccount.getName()
                        .equalsIgnoreCase(portfolioId)) {

                    // Check currencies
                    if (subAccount.getCurrencies()
                            .containsKey(assetSymbol)) {
                        balance = subAccount
                                .getBalanceOf(assetSymbol)
                                .doubleValue();
                        found = true;
                    }
                    else {
                        // Check stocks
                        for (final Asset asset
                                : subAccount.getAssets()) {
                            if (asset instanceof Stock
                                    && ((Stock) asset)
                                    .getCompanySymbol()
                                    .equalsIgnoreCase(assetSymbol)) {
                                balance = asset.getQuantity();
                                found = true;
                                break;
                            }
                        }
                    }
                }

                if (found) {
                    break;
                }
            }
        }

        return balance;
    }

    @Override
    public void transferAsset(final String username,
                              final String fromPortfolio,
                              final String toPortfolio,
                              final String assetSymbol,
                              final double amount) {

        final List<SubAccount> accounts =
                getUserAccountsOrThrow(username);
        final SubAccount from =
                findSubAccount(accounts, fromPortfolio);
        final SubAccount to =
                findSubAccount(accounts, toPortfolio);

        if (from == null || to == null) {
            throw new IllegalArgumentException(
                    "Portfolio not found.");
        }

        final Stock sourceStock =
                findStockInSubAccount(from, assetSymbol);

        if (sourceStock != null) {
            transferStock(from, to, sourceStock, assetSymbol, amount);
        }
        else {
            transferCurrency(from, to, assetSymbol, amount);
        }

        saveToFile();
    }

    private List<SubAccount> getUserAccountsOrThrow(
            final String username) {

        final List<SubAccount> accounts = data.get(username);
        if (accounts == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return accounts;
    }

    private SubAccount findSubAccount(final List<SubAccount> accounts,
                                      final String portfolioId) {

        SubAccount result = null;

        for (final SubAccount subAccount : accounts) {
            if (subAccount.getName().equals(portfolioId)) {
                result = subAccount;
                break;
            }
        }

        return result;
    }

    private Stock findStockInSubAccount(final SubAccount subAccount,
                                        final String assetSymbol) {

        Stock result = null;

        for (final Asset asset : subAccount.getAssets()) {
            if (asset instanceof Stock) {
                final Stock stock = (Stock) asset;
                if (stock.getCompanySymbol()
                        .equalsIgnoreCase(assetSymbol)) {
                    result = stock;
                    break;
                }
            }
        }

        return result;
    }

    private void transferStock(final SubAccount from,
                               final SubAccount tto,
                               final Stock sourceStock,
                               final String assetSymbol,
                               final double amount) {

        if (sourceStock.getQuantity() < amount) {
            throw new IllegalArgumentException(
                    "Insufficient stock quantity.");
        }

        final double remaining =
                sourceStock.getQuantity() - amount;
        sourceStock.setQuantity(remaining);

        if (remaining == 0.0) {
            from.removeAsset(sourceStock);
        }

        final Stock newStock =
                new Stock(assetSymbol, amount, assetSymbol);
        tto.addOrIncreaseAsset(newStock);
    }

    private void transferCurrency(final SubAccount from,
                                  final SubAccount tto,
                                  final String assetSymbol,
                                  final double amount) {

        final BigDecimal transferAmount =
                BigDecimal.valueOf(amount);
        final BigDecimal fromBalance =
                from.getBalanceOf(assetSymbol);

        if (fromBalance.compareTo(transferAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        from.setBalanceOf(
                assetSymbol,
                fromBalance.subtract(transferAmount));

        final BigDecimal toBalance =
                tto.getBalanceOf(assetSymbol);
        tto.setBalanceOf(
                assetSymbol,
                toBalance.add(transferAmount));
    }

    @Override
    public String[] getAvailablePortfolios(final String username) {
        final List<SubAccount> accounts =
                getSubAccountsOf(username);
        final String[] names = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            names[i] = accounts.get(i).getName();
        }
        return names;
    }

    @Override
    public String[] getAvailableStocks(final String username,
                                       final String portfolioId) {

        final List<String> symbols = new ArrayList<>();

        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount subAccount : accounts) {
                if (subAccount.getName().equals(portfolioId)) {
                    for (final Asset asset
                            : subAccount.getAssets()) {
                        if (asset instanceof Stock) {
                            symbols.add(
                                    ((Stock) asset)
                                            .getCompanySymbol());
                        }
                    }
                    break;
                }
            }
        }

        return symbols.toArray(new String[0]);
    }

    @Override
    public double getStockQuantity(final String username,
                                   final String portfolioName,
                                   final String stockName) {

        double quantity = 0.0;
        boolean found = false;

        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount subAccount : accounts) {
                if (subAccount.getName().equals(portfolioName)) {
                    for (final Asset asset
                            : subAccount.getAssets()) {

                        if (asset instanceof final Stock stock) {
                            if (stock.getCompanySymbol()
                                    .equals(stockName)) {
                                quantity = stock.getQuantity();
                                found = true;
                                break;
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }

        return quantity;
    }

    @Override
    public void updateStockQuantity(final String username,
                                    final String portfolioName,
                                    final String stockName,
                                    final double quantity) {

        boolean updated = false;

        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount subAccount : accounts) {
                if (subAccount.getName().equals(portfolioName)) {
                    for (final Asset asset
                            : subAccount.getAssets()) {

                        if (asset instanceof final Stock stock) {
                            if (stock.getCompanySymbol()
                                    .equals(stockName)) {
                                stock.setQuantity(quantity);
                                updated = true;
                                break;
                            }
                        }
                    }
                    if (updated) {
                        break;
                    }
                }
            }
        }

        if (updated) {
            saveToFile();
        }
    }

    @Override
    public void removeStock(final String username,
                            final String portfolioName,
                            final String stockName) {

        boolean removed = false;

        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount subAccount : accounts) {
                if (subAccount.getName().equals(portfolioName)) {

                    final Iterator<Asset> iter =
                            subAccount.getAssets().iterator();

                    while (iter.hasNext()) {
                        final Asset asset = iter.next();
                        if (asset instanceof final Stock stock) {
                            if (stock.getCompanySymbol()
                                    .equals(stockName)) {
                                subAccount.removeAsset(stock);
                                removed = true;
                                break;
                            }
                        }
                    }

                    if (removed) {
                        break;
                    }
                }
            }
        }

        if (removed) {
            saveToFile();
        }
    }

    @Override
    public void addCashToPortfolio(final String username,
                                   final String portfolioName,
                                   final double amount) {

        boolean updated = false;

        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount subAccount : accounts) {

                if (subAccount.getName().equals(portfolioName)) {

                    BigDecimal current =
                            subAccount.getBalanceOf(KEY_USD);
                    if (current == null) {
                        current = BigDecimal.ZERO;
                    }

                    final BigDecimal newBalance =
                            current.add(
                                    BigDecimal.valueOf(amount));

                    subAccount.setBalanceOf(KEY_USD, newBalance);

                    updated = true;
                    break;
                }
            }
        }

        if (updated) {
            saveToFile();
        }
    }

    @Override
    public String[] getAvailableCurrencies(final String username,
                                           final String portfolioId) {

        String[] result = {"USD"};

        final List<SubAccount> accounts = data.get(username);
        if (accounts != null) {
            for (final SubAccount subAccount : accounts) {
                if (subAccount.getName().equals(portfolioId)) {
                    final Set<String> keys =
                            subAccount.getCurrencies().keySet();
                    result = keys.toArray(new String[0]);
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public Map<String, Double> getCurrencies(final String username,
                                             final String accountName) {

        final List<SubAccount> list = data.get(username);
        if (list == null) {
            throw new RuntimeException(
                    "User not found: " + username);
        }

        for (final SubAccount sa : list) {
            if (sa.getName().equals(accountName)) {
                final Map<String, Double> map = new HashMap<>();
                for (final Map.Entry<String, BigDecimal> e
                        : sa.getCurrencies().entrySet()) {
                    map.put(
                            e.getKey(),
                            e.getValue().doubleValue());
                }
                return map;
            }
        }

        throw new RuntimeException(
                "Account not found: " + accountName);
    }

    @Override
    public void saveCurrencies(final String username,
                               final String accountName,
                               final Map<String, Double> currencies) {

        final List<SubAccount> list = data.get(username);
        if (list == null) {
            throw new RuntimeException(
                    "User not found: " + username);
        }

        boolean updated = false;

        for (final SubAccount subAccount : list) {
            if (subAccount.getName().equals(accountName)) {
                for (final Map.Entry<String, Double> entry
                        : currencies.entrySet()) {
                    subAccount.setBalanceOf(
                            entry.getKey(),
                            BigDecimal.valueOf(entry.getValue()));
                }
                updated = true;
                break;
            }
        }

        if (updated) {
            saveToFile();
        }
        else {
            throw new RuntimeException(
                    "Account not found: " + accountName);
        }
    }

    /**
     * Fetches exchange rates for the given currency.
     *
     * @param currency the base currency.
     * @return a mapping of currency codes to their exchange rates.
     * @throws RuntimeException if the API request fails.
     */
    @Override
    public Map<String, Double> getRates(final String currency) {

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://open.er-api.com/v6/latest/" + currency)
                .get()
                .build();

        final Map<String, Double> rates = new HashMap<>();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("API response error");
            }

            final JSONObject responseBody =
                    new JSONObject(response.body().string());

            if (responseBody.getString("result").equals("success")) {
                final JSONObject rateObject =
                        responseBody.getJSONObject("rates");
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
        catch (final IOException | JSONException evt) {
            throw new RuntimeException(
                    "Failed to fetch exchange rates", evt);
        }

        return rates;
    }
}
