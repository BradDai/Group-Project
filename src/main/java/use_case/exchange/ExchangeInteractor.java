package use_case.exchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import entity.SubAccount;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExchangeInteractor implements ExchangeInputBoundary {

    private final ExchangeOutputBoundary exchangePresenter;
    private final ExchangeDataAccessInterface exchangeDataAccess;

    public ExchangeInteractor(final ExchangeOutputBoundary exchangePresenter,
                              final ExchangeDataAccessInterface exchangeDataAccess) {
        this.exchangePresenter = exchangePresenter;
        this.exchangeDataAccess = exchangeDataAccess;
    }

    @Override
    public void fetchExchangeRate(final ExchangeInputData inputData) {
        try {
            final Map<String, Double> rates = getRates(inputData.getFrom());
            final Double rate = rates.get(inputData.getTo());

            if (rate == null) {
                exchangePresenter.presentFailure("Invalid target currency.");
            }
            else {
                final ExchangeOutputData outputData = new ExchangeOutputData(
                    inputData.getFrom(),
                    inputData.getTo(),
                    rate
                );
                exchangePresenter.presentSuccess(outputData);
            }

        }
        catch (final Exception e) {
            exchangePresenter.presentFailure("Error fetching rate: " + e.getMessage());
        }
    }

    @Override
    public void convert(final ExchangeConversionInputData inputData) {
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
            final Map<String, Double> currencies =
                exchangeDataAccess.getCurrencies(inputData.getUsername(), inputData.getAccountName());

            final Double fromBalance = currencies.get(inputData.getFrom());
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
            final Map<String, Double> rates = getRates(inputData.getFrom());
            final Double rate = rates.get(inputData.getTo());
            if (rate == null) {
                exchangePresenter.presentConversionFailure("Invalid target currency.");
                return;
            }

            final double amountGiven = inputData.getAmount();
            final double amountReceived = amountGiven * rate;

            final double fromAfter = fromBalance - amountGiven;
            final double toBefore = currencies.getOrDefault(inputData.getTo(), 0.0);
            final double toAfter = toBefore + amountReceived;

            currencies.put(inputData.getFrom(), fromAfter);
            currencies.put(inputData.getTo(), toAfter);

            // 3) Persist to JSON
            exchangeDataAccess.saveCurrencies(
                inputData.getUsername(),
                inputData.getAccountName(),
                currencies
            );
            // Get entire updated subaccount list for the user
            final List<SubAccount> updatedSubAccounts = exchangeDataAccess.getSubAccountsOf(inputData.getUsername());

            // 4) Build output data for presenter
            final ExchangeConversionOutputData outputData = new ExchangeConversionOutputData(
                inputData.getAccountName(),
                inputData.getFrom(),
                inputData.getTo(),
                amountGiven,
                amountReceived,
                rate,
                fromAfter,
                toAfter,
                updatedSubAccounts
            );
            exchangePresenter.presentConversionSuccess(outputData);

        }
        catch (final Exception e) {
            exchangePresenter.presentConversionFailure("Error during conversion: " + e.getMessage());
        }
    }

    // unchanged
    public Map<String, Double> getRates(final String currency) {

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
            .url("https://open.er-api.com/v6/latest/" + currency)
            .get()
            .build();

        final HashMap<String, Double> rates = new HashMap<>();

        try (final Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("API response error");
            }

            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getString("result").equals("success")) {
                final JSONObject rateObject = responseBody.getJSONObject("rates");
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
        catch (final IOException | JSONException e) {
            throw new RuntimeException("Failed to fetch exchange rates", e);
        }

        return rates;
    }
}
