package interfaceadapter.exchange;

import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.exchange.ExchangeConversionInputData;
import usecase.exchange.ExchangeInputBoundary;
import usecase.exchange.ExchangeInputData;

public class ExchangeController {

    private final ExchangeInputBoundary exchangeInputBoundary;
    private final LoggedInViewModel loggedInViewModel;

    public ExchangeController(final ExchangeInputBoundary exchangeInputBoundary,
                              final LoggedInViewModel loggedInViewModel) {
        this.exchangeInputBoundary = exchangeInputBoundary;
        this.loggedInViewModel = loggedInViewModel;
    }

    /**
     * Y.
     * @param from .
     * @param to .
     */
    public void getExchangeRate(final String from, final String to) {
        final ExchangeInputData inputData = new ExchangeInputData(from, to);
        exchangeInputBoundary.fetchExchangeRate(inputData);
    }

    /**
     * Y.
     * @param accountName .
     * @param from .
     * @param tto .
     * @param amount .
     */
    public void convert(final String accountName,
                        final String from,
                        final String tto,
                        final double amount) {

        final String username = loggedInViewModel.getState().getUsername();

        final ExchangeConversionInputData inputData =
            new ExchangeConversionInputData(username, accountName, from, tto, amount);
        exchangeInputBoundary.convert(inputData);
    }
}

