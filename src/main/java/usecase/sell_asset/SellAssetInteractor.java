package usecase.sell_asset;

import java.time.LocalDateTime;

import dataaccess.TransactionDataAccessInterface;
import entity.transaction.SellTransaction;

public class SellAssetInteractor implements SellAssetInputBoundary {
    private final SellAssetDataAccessInterface dataAccess;
    private final TransactionDataAccessInterface transactionDAO;   // ⭐ NEW
    private final SellAssetOutputBoundary sellAssetOutputBoundary;
    private final SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary;

    private double stockPrice = 0.0;

    public SellAssetInteractor(final SellAssetDataAccessInterface dataAccess,
                               final TransactionDataAccessInterface transactionDAO,   // ⭐ NEW
                               final SellAssetOutputBoundary sellAssetOutputBoundary,
                               final SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary) {
        this.dataAccess = dataAccess;
        this.transactionDAO = transactionDAO;   // ⭐ NEW
        this.sellAssetOutputBoundary = sellAssetOutputBoundary;
        this.sellAssetPriceOutputBoundary = sellAssetPriceOutputBoundary;
    }

    @Override
    public void execute(final SellAssetInputData sellAssetInputData) {
        final String username = sellAssetInputData.getUsername();
        final String portfolioName = sellAssetInputData.getportfolioName();
        final String stockName = sellAssetInputData.getAssetName();
        final double quantityToSell = sellAssetInputData.getQuantityToSell();
        final double currentQuantity =
            dataAccess.getStockQuantity(username, portfolioName, stockName);

        // validation
        if (username == null || username.isEmpty()) {
            sellAssetOutputBoundary.prepareFailureView("No user logged in.");
            return;
        }
        if (portfolioName == null || portfolioName.isEmpty()) {
            sellAssetOutputBoundary.prepareFailureView("Please choose a portfolio.");
            return;
        }
        if (stockName == null || stockName.isEmpty()) {
            sellAssetOutputBoundary.prepareFailureView("Please choose an asset.");
            return;
        }
        if (quantityToSell <= 0) {
            sellAssetOutputBoundary.prepareFailureView(
                "Invalid quantity: quantity to sell must be positive.");
            return;
        }
        if (quantityToSell > currentQuantity) {
            sellAssetOutputBoundary.prepareFailureView(
                "Invalid quantity: quantity to sell cannot exceed current quantity ("
                    + currentQuantity + ").");
            return;
        }
        if (stockPrice <= 0) {
            sellAssetOutputBoundary.prepareFailureView("Price not loaded.");
            return;
        }

        // update holdings & cash
        final double newQuantity = currentQuantity - quantityToSell;
        final double totalPrice = quantityToSell * stockPrice;

        dataAccess.updateStockQuantity(username, portfolioName, stockName, newQuantity);
        if (newQuantity == 0) {
            dataAccess.removeStock(username, portfolioName, stockName);
        }
        dataAccess.addCashToPortfolio(username, portfolioName, totalPrice);

        // ============================================================
        // ⭐ NEW: Save SELL transaction into transactionDAO
        // ============================================================
        final SellTransaction tx = new SellTransaction(
            generateTransactionId(),
            LocalDateTime.now(),
            portfolioName,     // fromPortfolio / portfolioName
            "Stock",
            stockName,
            quantityToSell,
            stockPrice         // price per unit
        );
        transactionDAO.save(username, tx);
        // ============================================================

        final SellAssetOutputData outputData = new SellAssetOutputData(
            username, stockName, quantityToSell, totalPrice, newQuantity
        );
        sellAssetOutputBoundary.prepareSuccessView(outputData);
    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis();
    }

    @Override
    public void fetchPrice(final String stockName) {
        try {
            // Jack's API key
            final String apiKey = "88ae0ec531a04cbc80652a7a22487707";
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

            final org.json.JSONObject json = new org.json.JSONObject(response.toString());
            stockPrice = json.getDouble("price");
            stockPrice = Math.round(stockPrice * 100) / 100.0;

            final SellAssetPriceOutputData outputData =
                new SellAssetPriceOutputData(stockPrice);
            sellAssetPriceOutputBoundary.preparePriceSuccessView(outputData);

        }
        catch (final Exception e) {
            sellAssetPriceOutputBoundary.preparePriceFailureView(
                "API Error: " + e.getMessage());
        }
    }
}
