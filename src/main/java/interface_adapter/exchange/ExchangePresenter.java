package interface_adapter.exchange;

import use_case.exchange.ExchangeOutputBoundary;
import use_case.exchange.ExchangeOutputData;

public class ExchangePresenter implements ExchangeOutputBoundary {

    private final ExchangeViewModel exchangeViewModel;

    public ExchangePresenter(ExchangeViewModel exchangeViewModel) {
        this.exchangeViewModel = exchangeViewModel;
    }

    @Override
    public void presentSuccess(ExchangeOutputData outputData) {
        String formatted = String.format(
                "1 %s = %.4f %s",
                outputData.getFrom(),
                outputData.getRate(),
                outputData.getTo()
        );
        exchangeViewModel.setExchangeRate(formatted);
        exchangeViewModel.firePropertyChanged();
    }

    @Override
    public void presentFailure(String errorMessage) {
        exchangeViewModel.setExchangeRate("Error: " + errorMessage);
        exchangeViewModel.firePropertyChanged();
    }
}
