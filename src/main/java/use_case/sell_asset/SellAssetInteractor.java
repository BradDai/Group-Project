package use_case.sell_asset;

public class SellAssetInteractor implements SellAssetInputBoundary {
    private final SellAssetOutputBoundary sellAssetOutputBoundary;
    private final SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary;

    private String apiKey = "demo"; // TODO: replace with real API Key
    private double stockPrice = 0.0;

    public SellAssetInteractor(SellAssetOutputBoundary sellAssetOutputBoundary,
                               SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary) {
        this.sellAssetOutputBoundary = sellAssetOutputBoundary;
        this.sellAssetPriceOutputBoundary = sellAssetPriceOutputBoundary;
    }

    public void execute(SellAssetInputData sellAssetInputData) {
        // TODO: implement
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
            double price = json.getDouble("price");

            // Send to Presenter (Output Boundary)
            SellAssetPriceOutputData outputData = new SellAssetPriceOutputData(price);
            sellAssetPriceOutputBoundary.preparePriceSuccessView(outputData);

        } catch (Exception e) {
            // Report failure to presenter
            sellAssetPriceOutputBoundary.preparePriceFailureView("API Error: " + e.getMessage());
        }
    }
}
