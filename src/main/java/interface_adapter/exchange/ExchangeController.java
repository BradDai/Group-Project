package interface_adapter.exchange;

import use_case.exchange.ExchangeInputBoundary;
import use_case.exchange.ExchangeInputData;
import use_case.exchange.ExchangeConversionInputData;

public class ExchangeController {

    private final ExchangeInputBoundary exchangeInputBoundary;

    public ExchangeController(ExchangeInputBoundary exchangeInputBoundary) {
        this.exchangeInputBoundary = exchangeInputBoundary;
    }

    public void getExchangeRate(String from, String to) {
        ExchangeInputData inputData = new ExchangeInputData(from, to);
        exchangeInputBoundary.fetchExchangeRate(inputData);
    }

    public void convert(String username,
                        String accountName,
                        String from,
                        String to,
                        double amount) {
        ExchangeConversionInputData inputData =
                new ExchangeConversionInputData(username, accountName, from, to, amount);
        exchangeInputBoundary.convert(inputData);
    }
}
