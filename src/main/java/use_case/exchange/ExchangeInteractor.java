package use_case.exchange;

public class ExchangeInteractor implements ExchangeInputBoundary {
    private final ExchangeOutputBoundary exchangePresenter;

    public ExchangeInteractor(ExchangeOutputBoundary exchangeOutputBoundary) {
        exchangePresenter = exchangeOutputBoundary;
    }

    public void execute() {
        exchangePresenter.prepareSuccessView();
    }
}
