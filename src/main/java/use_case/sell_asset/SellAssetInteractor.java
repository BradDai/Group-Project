package use_case.sell_asset;

public class SellAssetInteractor implements SellAssetInputBoundary {
    private final SellAssetDataAccessInterface dataAccess;
    private final SellAssetOutputBoundary sellAssetOutputBoundary;
    private final SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary;

    private String apiKey = "demo"; // TODO: replace with real API Key
    private double stockPrice = 0.0;

    public SellAssetInteractor(SellAssetDataAccessInterface dataAccess,
                               SellAssetOutputBoundary sellAssetOutputBoundary,
                               SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary) {
        this.dataAccess = dataAccess;
        this.sellAssetOutputBoundary = sellAssetOutputBoundary;
        this.sellAssetPriceOutputBoundary = sellAssetPriceOutputBoundary;
    }

    public void execute(SellAssetInputData sellAssetInputData) {
        String username = sellAssetInputData.getUsername();
        String portfolioName = sellAssetInputData.getportfolioName();
        String stockName = sellAssetInputData.getAssetName();
        double quantityToSell = sellAssetInputData.getQuantityToSell();
        double currentQuantity = dataAccess.getStockQuantity(username, portfolioName, stockName);

        // handle exceptions
        if (quantityToSell <= 0) {
            sellAssetPriceOutputBoundary.preparePriceFailureView("Invalid Quantity to Sell: Quantity to sell must be positive.");
            return;
        }

        if (quantityToSell > currentQuantity) {
            sellAssetPriceOutputBoundary.preparePriceFailureView("Invalid Quantity to Sell: Quantity to sell must be greater than current Quantity.");
            return;
        }

        // correct quantity
        double newQuantity = currentQuantity - quantityToSell;
        double totalPrice = quantityToSell * stockPrice;

        if (newQuantity == 0) {
            dataAccess.removeStockIfZero(username, portfolioName, stockName);
        } else {
            dataAccess.updateStockQuantity(username, portfolioName, stockName, newQuantity);
        }
        dataAccess.addCashToPortfolio(username, portfolioName, totalPrice);

        // prepare output data
        SellAssetOutputData outputData = new SellAssetOutputData(
            username, quantityToSell, totalPrice, newQuantity
        );

        sellAssetOutputBoundary.prepareSuccessView(outputData);
    }

    @Override
    public void fetchPrice(String stockName) {
        try {
            String url = "https://api.twelvedata.com/price?symbol="
                    + stockName + "&apikey=" + apiKey;

            java.net.URL requestUrl = new java.net.URL(url);
            java.net.HttpURLConnection connection =
                    (java.net.HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");

            java.io.BufferedReader in =
                    new java.io.BufferedReader(
                            new java.io.InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            org.json.JSONObject json = new org.json.JSONObject(response.toString());
            stockPrice = json.getDouble("price");

            // Send to Presenter (Output Boundary)
            SellAssetPriceOutputData outputData = new SellAssetPriceOutputData(stockPrice);
            sellAssetPriceOutputBoundary.preparePriceSuccessView(outputData);

        } catch (Exception e) {
            // Report failure to presenter
            sellAssetPriceOutputBoundary.preparePriceFailureView("API Error: " + e.getMessage());
        }
    }
}
