package interface_adapter.exchange;

import use_case.exchange.ExchangeConversionInputData;
import use_case.exchange.ExchangeInputBoundary;
import use_case.exchange.ExchangeInputData;

public class ExchangeController {

    private final ExchangeInputBoundary exchangeInputBoundary;

    public ExchangeController(final ExchangeInputBoundary exchangeInputBoundary) {
        this.exchangeInputBoundary = exchangeInputBoundary;
    }

    public void getExchangeRate(final String from, final String to) {
        final ExchangeInputData inputData = new ExchangeInputData(from, to);
        exchangeInputBoundary.fetchExchangeRate(inputData);
    }

    public void convert(final String username,
                        final String accountName,
                        final String from,
                        final String to,
                        final double amount) {
        final ExchangeConversionInputData inputData =
            new ExchangeConversionInputData(username, accountName, from, to, amount);
        exchangeInputBoundary.convert(inputData);
    }
}
