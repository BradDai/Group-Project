package use_case.sell_asset;

import java.io.IOException;
import java.time.LocalDateTime;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import data_access.TransactionDataAccessInterface;
import entity.transaction.SellTransaction;

public class SellAssetInteractor implements SellAssetInputBoundary {
    private final SellAssetDataAccessInterface dataAccess;
    private final TransactionDataAccessInterface transactionDataAccess;
    private final SellAssetOutputBoundary sellAssetOutputBoundary;
    private final SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary;

    private double stockPrice;

    public SellAssetInteractor(final SellAssetDataAccessInterface dataAccess,
                               final TransactionDataAccessInterface transactionDataAccess,
                               final SellAssetOutputBoundary sellAssetOutputBoundary,
                               final SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary) {
        this.dataAccess = dataAccess;
        this.transactionDataAccess = transactionDataAccess;
        this.sellAssetOutputBoundary = sellAssetOutputBoundary;
        this.sellAssetPriceOutputBoundary = sellAssetPriceOutputBoundary;
    }

    @Override
    public void execute(final SellAssetInputData sellAssetInputData) {
        boolean valid = true;

        final String username = sellAssetInputData.getUsername();
        final String portfolioName = sellAssetInputData.getportfolioName();
        final String stockName = sellAssetInputData.getAssetName();
        final double quantityToSell = sellAssetInputData.getQuantityToSell();
        final double currentQuantity =
                dataAccess.getStockQuantity(username, portfolioName, stockName);

        // validation
        if (username == null || username.isEmpty()) {
            sellAssetOutputBoundary.prepareFailureView("No user logged in.");
            valid = false;
        }
        if (portfolioName == null || portfolioName.isEmpty()) {
            sellAssetOutputBoundary.prepareFailureView("Please choose a portfolio.");
            valid = false;
        }
        if (stockName == null || stockName.isEmpty()) {
            sellAssetOutputBoundary.prepareFailureView("Please choose an asset.");
            valid = false;
        }
        if (quantityToSell <= 0) {
            sellAssetOutputBoundary.prepareFailureView(
                    "Invalid quantity: quantity to sell must be positive.");
            valid = false;
        }
        if (quantityToSell > currentQuantity) {
            sellAssetOutputBoundary.prepareFailureView(
                    "Invalid quantity: quantity to sell cannot exceed current quantity ("
                            + currentQuantity + ").");
            valid = false;
        }
        if (stockPrice <= 0) {
            sellAssetOutputBoundary.prepareFailureView("Price not loaded.");
            valid = false;
        }

        // update holdings & cash
        if (valid) {
            final double newQuantity = currentQuantity - quantityToSell;
            final double totalPrice = quantityToSell * stockPrice;

            dataAccess.updateStockQuantity(username, portfolioName, stockName, newQuantity);
            if (newQuantity == 0) {
                dataAccess.removeStock(username, portfolioName, stockName);
            }
            dataAccess.addCashToPortfolio(username, portfolioName, totalPrice);

            // ============================================================
            // ⭐ NEW: Save SELL transaction into transactionDataAccess
            // ============================================================
            final SellTransaction tx = new SellTransaction(
                    generateTransactionId(),
                    LocalDateTime.now(),
                    portfolioName,
                    "Stock",
                    stockName,
                    quantityToSell,
                    stockPrice
            );
            transactionDataAccess.save(username, tx);
            // ============================================================

            final SellAssetOutputData outputData = new SellAssetOutputData(
                    username, stockName, quantityToSell, totalPrice, newQuantity
            );
            sellAssetOutputBoundary.prepareSuccessView(outputData);
        }
    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis();
    }

    @Override
    public void fetchPrice(final String stockName) {
        try {
            // Jack's API key
            final String apiKey = "88ae0ec531a04cbc80652a7a22487707";
            final JSONObject json = getJsonObject(stockName, apiKey);
            stockPrice = json.getDouble("price");
            final int intHundred = 100;
            final double doubleHundred = 100.0;
            stockPrice = Math.round(stockPrice * intHundred) / doubleHundred;

            final SellAssetPriceOutputData outputData =
                    new SellAssetPriceOutputData(stockPrice);
            sellAssetPriceOutputBoundary.preparePriceSuccessView(outputData);

        }
        catch (IOException ex) {
            sellAssetPriceOutputBoundary.preparePriceFailureView("API Error: " + ex.getMessage());
        }
    }

    @NotNull
    private static JSONObject getJsonObject(String stockName, String apiKey) throws IOException {
        final String url = "https://api.twelvedata.com/price?symbol="
                + stockName + "&apikey=" + apiKey;

        final java.net.URL requestUrl = new java.net.URL(url);
        final java.net.HttpURLConnection connection =
                (java.net.HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");

        final java.io.BufferedReader in =
                new java.io.BufferedReader(
                        new java.io.InputStreamReader(connection.getInputStream()));

        final StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        final JSONObject json = new JSONObject(response.toString());
        return json;
    }
}
