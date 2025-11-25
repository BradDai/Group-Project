package use_case.exchange;

import java.util.List;
import java.util.Map;
import entity.SubAccount;

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
            final Map<String, Double> rates = exchangeDataAccess.getRates(inputData.getFrom());
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

            final Map<String, Double> rates = exchangeDataAccess.getRates(inputData.getFrom());
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

            exchangeDataAccess.saveCurrencies(
                inputData.getUsername(),
                inputData.getAccountName(),
                currencies
            );

            final List<SubAccount> updatedSubAccounts = exchangeDataAccess.getSubAccountsOf(inputData.getUsername());

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
}
