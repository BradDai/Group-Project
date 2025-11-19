package use_case.exchange;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExchangeInteractor implements ExchangeInputBoundary {

    private final ExchangeOutputBoundary exchangePresenter;

    public ExchangeInteractor(ExchangeOutputBoundary exchangePresenter) {
        this.exchangePresenter = exchangePresenter;
    }

    @Override
    public void fetchExchangeRate(ExchangeInputData inputData) {
        try {
            Map<String, Double> rates = getRates(inputData.getFrom());
            Double rate = rates.get(inputData.getTo());

            if (rate == null) {
                exchangePresenter.presentFailure("Invalid target currency.");
            }

            else {
                ExchangeOutputData outputData = new ExchangeOutputData(
                        inputData.getFrom(),
                        inputData.getTo(),
                        rate
                );
                exchangePresenter.presentSuccess(outputData);
            }

        } catch (Exception e) {
            exchangePresenter.presentFailure("Error fetching rate: " + e.getMessage());
        }


    }

    public Map<String, Double> getRates(String currency) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://open.er-api.com/v6/latest/" + currency)
                .get()
                .build();

        HashMap<String, Double> rates = new HashMap<>();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("API response error");
            }

            JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getString("result").equals("success")) {
                JSONObject rateObject = responseBody.getJSONObject("rates");
                Iterator<String> keys = rateObject.keys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    rates.put(key, rateObject.getDouble(key));
                }
            } else {
                throw new RuntimeException("API returned failure");
            }

        } catch (IOException | JSONException e) {
            throw new RuntimeException("Failed to fetch exchange rates", e);
        }

        return rates;
    }
}
