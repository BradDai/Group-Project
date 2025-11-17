package interface_adapter.exchange;

import use_case.exchange.ExchangeInputBoundary;

public class ExchangeController {

    private final ExchangeInputBoundary exchangeUseCaseInteractor;

    public ExchangeController(ExchangeInputBoundary exchangeUseCaseInteractor) {
        this.exchangeUseCaseInteractor = exchangeUseCaseInteractor;
    }

    public void execute() {
        exchangeUseCaseInteractor.execute();
    }
}
