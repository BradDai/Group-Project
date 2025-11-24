package use_case.exchange;

import entity.SubAccount;
import java.util.List;
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
            Map<String, Double> rates = exchangeDataAccess.getRates(inputData.getFrom());
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

            Map<String, Double> rates = exchangeDataAccess.getRates(inputData.getFrom());
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

            exchangeDataAccess.saveCurrencies(
                    inputData.getUsername(),
                    inputData.getAccountName(),
                    currencies
            );

            List<SubAccount> updatedSubAccounts = exchangeDataAccess.getSubAccountsOf(inputData.getUsername());

            ExchangeConversionOutputData outputData = new ExchangeConversionOutputData(
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

        } catch (Exception e) {
            exchangePresenter.presentConversionFailure("Error during conversion: " + e.getMessage());
        }
    }
}
