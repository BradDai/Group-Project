package usecase.exchange;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import dataaccess.TransactionDataAccessInterface;
import entity.SubAccount;
import entity.transaction.ConvertTransaction;

public class ExchangeInteractor implements ExchangeInputBoundary {

    private final ExchangeOutputBoundary exchangePresenter;
    private final ExchangeDataAccessInterface exchangeDataAccess;
    private final TransactionDataAccessInterface transactionDAO;

    public ExchangeInteractor(final ExchangeOutputBoundary exchangePresenter,
                              final ExchangeDataAccessInterface exchangeDataAccess,
                              final TransactionDataAccessInterface transactionDAO) {
        this.exchangePresenter = exchangePresenter;
        this.exchangeDataAccess = exchangeDataAccess;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public void fetchExchangeRate(final ExchangeInputData inputData) {
        try {
            final Map<String, Double> rates = exchangeDataAccess.getRates(inputData.from());
            final Double rate = rates.get(inputData.to());

            if (rate == null) {
                exchangePresenter.presentFailure("Invalid target currency.");
            }
            else {
                final ExchangeOutputData outputData = new ExchangeOutputData(
                    inputData.from(),
                    inputData.to(),
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
            if (inputData.amount() <= 0) {
                exchangePresenter.presentConversionFailure("Amount must be positive.");
                return;
            }
            if (inputData.from().equals(inputData.to())) {
                exchangePresenter.presentConversionFailure("Source and target currency must be different.");
                return;
            }

            final Map<String, Double> currencies =
                exchangeDataAccess.getCurrencies(inputData.username(), inputData.accountName());

            final Double fromBalance = currencies.get(inputData.from());
            if (fromBalance == null) {
                exchangePresenter.presentConversionFailure(
                    "Account does not own currency: " + inputData.from());
                return;
            }
            if (fromBalance < inputData.amount()) {
                exchangePresenter.presentConversionFailure("Insufficient " + inputData.from() + " balance.");
                return;
            }

            final Map<String, Double> rates =
                exchangeDataAccess.getRates(inputData.from());
            final Double rate = rates.get(inputData.to());
            if (rate == null) {
                exchangePresenter.presentConversionFailure("Invalid target currency.");
                return;
            }

            final double amountGiven = inputData.amount();
            final double amountReceived = amountGiven * rate;

            final double fromAfter = fromBalance - amountGiven;
            final double toBefore = currencies.getOrDefault(inputData.to(), 0.0);
            final double toAfter = toBefore + amountReceived;

            currencies.put(inputData.from(), fromAfter);
            currencies.put(inputData.to(), toAfter);

            exchangeDataAccess.saveCurrencies(
                inputData.username(),
                inputData.accountName(),
                currencies
            );

            //  NEW: record convert transaction in history
            final ConvertTransaction tx = new ConvertTransaction(
                "TX-" + System.currentTimeMillis(),
                LocalDateTime.now(),
                inputData.accountName(),
                inputData.from(),
                inputData.to(),
                amountGiven,
                rate
            );

            transactionDAO.save(inputData.username(), tx);

            final List<SubAccount> updatedSubAccounts =
                exchangeDataAccess.getSubAccountsOf(inputData.username());

            final ExchangeConversionOutputData outputData = new ExchangeConversionOutputData(
                inputData.accountName(),
                inputData.from(),
                inputData.to(),
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

