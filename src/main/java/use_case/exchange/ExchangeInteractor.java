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
    private final ExchangeDataAccessInterface exchangeDataAccess;

    public ExchangeInteractor(ExchangeOutputBoundary exchangePresenter,
                              ExchangeDataAccessInterface exchangeDataAccess) {
        this.exchangePresenter = exchangePresenter;
        this.exchangeDataAccess = exchangeDataAccess;
    }

    @Override
    public void fetchExchangeRate(ExchangeInputData inputData) {
        try {
            Map<String, Double> rates = getRates(inputData.getFrom());
            Double rate = rates.get(inputData.getTo());

            if (rate == null) {
                exchangePresenter.presentFailure("Invalid target currency.");
            } else {
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

    @Override
    public void convert(ExchangeConversionInputData inputData) {
        try {
            if (inputData.getAmount() <= 0) {
                exchangePresenter.presentConversionFailure("Amount must be positive.");
                return;
            }
            if (inputData.getFrom().equals(inputData.getTo())) {
                exchangePresenter.presentConversionFailure("Source and target currency must be different.");
                return;
            }

            // 1) Load current balances for this subaccount
            Map<String, Double> currencies =
                    exchangeDataAccess.getCurrencies(inputData.getUsername(), inputData.getAccountName());

            Double fromBalance = currencies.get(inputData.getFrom());
            if (fromBalance == null) {
                exchangePresenter.presentConversionFailure(
                        "Account does not own currency: " + inputData.getFrom());
                return;
            }
            if (fromBalance < inputData.getAmount()) {
                exchangePresenter.presentConversionFailure("Insufficient " + inputData.getFrom() + " balance.");
                return;
            }

            // 2) Get rate from API
            Map<String, Double> rates = getRates(inputData.getFrom());
            Double rate = rates.get(inputData.getTo());
            if (rate == null) {
                exchangePresenter.presentConversionFailure("Invalid target currency.");
                return;
            }

            double amountGiven = inputData.getAmount();
            double amountReceived = amountGiven * rate;

            double fromAfter = fromBalance - amountGiven;
            double toBefore = currencies.getOrDefault(inputData.getTo(), 0.0);
            double toAfter = toBefore + amountReceived;

            currencies.put(inputData.getFrom(), fromAfter);
            currencies.put(inputData.getTo(), toAfter);

            // 3) Persist to JSON
            exchangeDataAccess.saveCurrencies(
                    inputData.getUsername(),
                    inputData.getAccountName(),
                    currencies
            );

            // 4) Build output data for presenter
            ExchangeConversionOutputData outputData = new ExchangeConversionOutputData(
                    inputData.getAccountName(),
                    inputData.getFrom(),
                    inputData.getTo(),
                    amountGiven,
                    amountReceived,
                    rate,
                    fromAfter,
                    toAfter
            );
            exchangePresenter.presentConversionSuccess(outputData);

        } catch (Exception e) {
            exchangePresenter.presentConversionFailure("Error during conversion: " + e.getMessage());
        }
    }

    // unchanged
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
