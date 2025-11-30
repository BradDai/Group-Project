package usecase.sell_asset;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import dataaccess.TransactionDataAccessInterface;
import entity.Stock;
import entity.SubAccount;
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
        // 1. Validate input
        if (!validateInput(sellAssetInputData)) {
            return;
        }

        // 2. Load the SubAccount entity
        final String username = sellAssetInputData.getUsername();
        final String portfolioName = sellAssetInputData.getportfolioName();
        final String stockName = sellAssetInputData.getAssetName();
        final double quantityToSell = sellAssetInputData.getQuantityToSell();

        final SubAccount portfolio = loadPortfolio(username, portfolioName);
        if (portfolio == null) {
            sellAssetOutputBoundary.prepareFailureView("Portfolio not found.");
            return;
        }

        // 3. Check if portfolio has the stock
        if (!portfolio.hasStock(stockName)) {
            sellAssetOutputBoundary.prepareFailureView("Stock not found in portfolio.");
            return;
        }

        final Stock stock = portfolio.findStock(stockName);

        // 4. Validate using entity business logic
        if (!stock.canSell(quantityToSell)) {
            sellAssetOutputBoundary.prepareFailureView(
                    "Cannot sell " + quantityToSell + " units. Available: " + stock.getQuantity()
            );
            return;
        }

        // 5. Execute the sale using entity methods
        try {
            portfolio.sellStock(stockName, quantityToSell, stockPrice);
        }
        catch (final IllegalArgumentException ex) {
            sellAssetOutputBoundary.prepareFailureView(ex.getMessage());
            return;
        }

        // 6. Save the updated entity
        dataAccess.save(username, portfolio);

        // 7. Save transaction
        saveTransaction(username, portfolioName, stockName, quantityToSell);

        // 8. Prepare success response
        final double totalPrice = quantityToSell * stockPrice;
        final double remainingQuantity = portfolio.getStockQuantity(stockName);

        final SellAssetOutputData outputData = new SellAssetOutputData(
                username, stockName, quantityToSell, totalPrice, remainingQuantity
        );
        sellAssetOutputBoundary.prepareSuccessView(outputData);
    }

    private SubAccount loadPortfolio(final String username, final String portfolioName) {
        final List<SubAccount> accounts = dataAccess.getSubAccountsOf(username);
        SubAccount portfolio = null;
        for (final SubAccount sa : accounts) {
            if (sa.getName().equalsIgnoreCase(portfolioName)) {
                portfolio = sa;
            }
        }
        return portfolio;
    }

    private boolean validateInput(final SellAssetInputData data) {
        boolean valid = true;

        if (!checkUsername(data.getUsername())) {
            valid = false;
        }

        if (!checkPortfolio(data.getportfolioName())) {
            valid = false;
        }

        if (!checkStockName(data.getAssetName())) {
            valid = false;
        }

        if (!checkQuantityPositive(data.getQuantityToSell())) {
            valid = false;
        }

        if (!checkPriceLoaded(stockPrice)) {
            valid = false;
        }

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

    private boolean checkPriceLoaded(final double price) {
        boolean valid = true;
        if (price <= 0) {
            sellAssetPriceOutputBoundary.preparePriceFailureView("Price not loaded.");
            valid = false;
        }
        return valid;
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
