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
        final boolean isValid = validateInput(sellAssetInputData);
        if (isValid) {
            processSale(sellAssetInputData);
        }
    }

    private boolean validateInput(final SellAssetInputData data) {
        boolean valid = true;

        valid &= checkUsername(data.getUsername());
        valid &= checkPortfolio(data.getportfolioName());
        valid &= checkStockName(data.getAssetName());
        valid &= checkQuantityPositive(data.getQuantityToSell());
        valid &= checkQuantityAvailable(
                data.getQuantityToSell(),
                this.dataAccess.getStockQuantity(data.getUsername(), data.getportfolioName(), data.getAssetName())
        );
        valid &= checkPriceLoaded(stockPrice);

        return valid;
    }

    private boolean checkUsername(final String username) {
        boolean valid = true;
        if (username == null || username.isEmpty()) {
            sellAssetOutputBoundary.prepareFailureView("No user logged in.");
            valid = false;
        }
        return valid;
    }

    private boolean checkPortfolio(final String portfolioName) {
        boolean valid = true;
        if (portfolioName == null || portfolioName.isEmpty()) {
            sellAssetOutputBoundary.prepareFailureView("Please choose a portfolio.");
            valid = false;
        }
        return valid;
    }

    private boolean checkStockName(final String stockName) {
        boolean valid = true;
        if (stockName == null || stockName.isEmpty()) {
            sellAssetOutputBoundary.prepareFailureView("Please choose an asset.");
            valid = false;
        }
        return valid;
    }

    private boolean checkQuantityPositive(final double quantity) {
        boolean valid = true;
        if (quantity <= 0) {
            sellAssetOutputBoundary.prepareFailureView("Invalid quantity: quantity must be positive.");
            valid = false;
        }
        return valid;
    }

    private boolean checkQuantityAvailable(final double quantity, final double current) {
        boolean valid = true;
        if (quantity > current) {
            sellAssetOutputBoundary.prepareFailureView(
                    "Invalid quantity: cannot exceed current quantity (" + current + ")."
            );
            valid = false;
        }
        return valid;
    }

    private boolean checkPriceLoaded(final double price) {
        boolean valid = true;
        if (price <= 0) {
            sellAssetOutputBoundary.prepareFailureView("Price not loaded.");
            valid = false;
        }
        return valid;
    }

    private void processSale(final SellAssetInputData data) {
        final String username = data.getUsername();
        final String portfolioName = data.getportfolioName();
        final String stockName = data.getAssetName();
        final double quantityToSell = data.getQuantityToSell();
        final double currentQuantity =
                this.dataAccess.getStockQuantity(username, portfolioName, stockName);

        final double newQuantity = currentQuantity - quantityToSell;
        final double totalPrice = quantityToSell * stockPrice;

        dataAccess.updateStockQuantity(username, portfolioName, stockName, newQuantity);
        if (newQuantity == 0) {
            dataAccess.removeStock(username, portfolioName, stockName);
        }
        dataAccess.addCashToPortfolio(username, portfolioName, totalPrice);

        saveTransaction(username, portfolioName, stockName, quantityToSell);

        final SellAssetOutputData outputData =
                new SellAssetOutputData(username, stockName, quantityToSell, totalPrice, newQuantity);
        sellAssetOutputBoundary.prepareSuccessView(outputData);
    }

    private void saveTransaction(final String username,
                                 final String portfolioName,
                                 final String stockName,
                                 final double quantityToSell) {
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
    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis();
    }

    @Override
    public void fetchPrice(final String stockName) {
        try {
            final JSONObject json = getJsonObject(stockName);
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
    private static JSONObject getJsonObject(String stockName) throws IOException {
        final String url = "https://api.twelvedata.com/price?symbol="
                + stockName + "&apikey=" + "88ae0ec531a04cbc80652a7a22487707";

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

        return new JSONObject(response.toString());
    }
}
