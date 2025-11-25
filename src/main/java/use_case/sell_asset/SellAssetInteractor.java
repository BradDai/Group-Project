package use_case.sell_asset;

public class SellAssetInteractor implements SellAssetInputBoundary {
    private final SellAssetDataAccessInterface dataAccess;
    private final SellAssetOutputBoundary sellAssetOutputBoundary;
    private final SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary;

    private final String apiKey = "demo"; // TODO: replace with real API Key
    private double stockPrice = 0.0;

    public SellAssetInteractor(final SellAssetDataAccessInterface dataAccess,
                               final SellAssetOutputBoundary sellAssetOutputBoundary,
                               final SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary) {
        this.dataAccess = dataAccess;
        this.sellAssetOutputBoundary = sellAssetOutputBoundary;
        this.sellAssetPriceOutputBoundary = sellAssetPriceOutputBoundary;
    }

    public void execute(final SellAssetInputData sellAssetInputData) {
        final String username = sellAssetInputData.getUsername();
        final String portfolioName = sellAssetInputData.getportfolioName();
        final String stockName = sellAssetInputData.getAssetName();
        final double quantityToSell = sellAssetInputData.getQuantityToSell();
        final double currentQuantity = dataAccess.getStockQuantity(username, portfolioName, stockName);

        // handle exceptions
        if (quantityToSell <= 0) {
            sellAssetOutputBoundary.prepareFailureView(
                "Invalid Quantity to Sell: Quantity to sell must be positive.");
            return;
        }

        if (quantityToSell > currentQuantity) {
            sellAssetOutputBoundary.prepareFailureView(
                "Invalid Quantity to Sell: Quantity to sell must be greater than current Quantity.");
            return;
        }

        // correct quantity
        final double newQuantity = currentQuantity - quantityToSell;
        final double totalPrice = quantityToSell * stockPrice;

        if (newQuantity == 0) {
            dataAccess.removeStockIfZero(username, portfolioName, stockName);
        }
        else {
            dataAccess.updateStockQuantity(username, portfolioName, stockName, newQuantity);
        }
        dataAccess.addCashToPortfolio(username, portfolioName, totalPrice);

        // prepare output data
        final SellAssetOutputData outputData = new SellAssetOutputData(
            username, stockName, quantityToSell, totalPrice, newQuantity
        );

        sellAssetOutputBoundary.prepareSuccessView(outputData);
    }

    @Override
    public void fetchPrice(final String stockName) {
        try {
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

            // Send to Presenter (Output Boundary)
            final SellAssetPriceOutputData outputData = new SellAssetPriceOutputData(stockPrice);
            sellAssetPriceOutputBoundary.preparePriceSuccessView(outputData);

        }
        catch (final Exception e) {
            // Report failure to presenter
            sellAssetPriceOutputBoundary.preparePriceFailureView("API Error: " + e.getMessage());
        }
    }
}
