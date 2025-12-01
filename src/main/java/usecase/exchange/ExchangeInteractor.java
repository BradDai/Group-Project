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
    private final TransactionDataAccessInterface transactionDao;

    public ExchangeInteractor(final ExchangeOutputBoundary exchangePresenter,
                              final ExchangeDataAccessInterface exchangeDataAccess,
                              final TransactionDataAccessInterface transactionDao) {
        this.exchangePresenter = exchangePresenter;
        this.exchangeDataAccess = exchangeDataAccess;
        this.transactionDao = transactionDao;
    }

    @Override
    // This could be improved by using some refactoring techniques. For example, the rates and rate variable could be
    // made inline, as the rates is only being used once, and rate twice.
    public void fetchExchangeRate(final ExchangeInputData inputData) {
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

    @Override
    // This code has a lot of nested conditions at the beginning, and this could definitely be improved. One example is
    // implementing a design pattern, like the Chain of Responsibility behavioral pattern. This has been implemented
    // in the TransferInteractor to check for all the conditions and validation required, and this could be done here
    // as well.
    public void convert(final ExchangeConversionInputData inputData) {

        if (inputData.amount() <= 0) {
            exchangePresenter.presentConversionFailure("Amount must be positive.");
        }
        else if (inputData.from().equals(inputData.to())) {
            exchangePresenter.presentConversionFailure(
                "Source and target currency must be different.");
        }
        else {
            final Map<String, Double> currencies =
                exchangeDataAccess.getCurrencies(
                    inputData.username(), inputData.accountName());
            final Double fromBalance = currencies.get(inputData.from());
            if (fromBalance == null) {
                exchangePresenter.presentConversionFailure(
                    "Account does not own currency: " + inputData.from());
            }
            else if (fromBalance < inputData.amount()) {
                exchangePresenter.presentConversionFailure(
                    "Insufficient " + inputData.from() + " balance.");
            }
            else {
                final Map<String, Double> rates =
                    exchangeDataAccess.getRates(inputData.from());
                final Double rate = rates.get(inputData.to());
                if (rate == null) {
                    exchangePresenter.presentConversionFailure("Invalid target currency.");
                }
                else {
                    // All variables are being used less than 3 times, so you could apply refactoring techniques such
                    // as inline variable, to improve the code quality.
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
                    final ConvertTransaction tx = new ConvertTransaction(
                        "TX-" + System.currentTimeMillis(),
                        LocalDateTime.now(),
                        inputData.accountName(),
                        inputData.from(),
                        inputData.to(),
                        amountGiven,
                        rate
                    );
                    transactionDao.save(inputData.username(), tx);
                    final List<SubAccount> updatedSubAccounts =
                        exchangeDataAccess.getSubAccountsOf(inputData.username());
                    final ExchangeConversionOutputData outputData =
                        new ExchangeConversionOutputData(
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
            }
        }
    }
}

