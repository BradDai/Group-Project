package interface_adapter.exchange;

import use_case.exchange.ExchangeInputBoundary;
import use_case.exchange.ExchangeInputData;

public class ExchangeController {

    private final ExchangeInputBoundary exchangeInputBoundary;

    public ExchangeController(ExchangeInputBoundary exchangeInputBoundary) {
        this.exchangeInputBoundary = exchangeInputBoundary;
    }

    public void getExchangeRate(String from, String to) {
        ExchangeInputData inputData = new ExchangeInputData(from, to);
        exchangeInputBoundary.fetchExchangeRate(inputData);
    }
}
