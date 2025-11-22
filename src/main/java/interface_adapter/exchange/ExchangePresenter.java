package interface_adapter.exchange;

import use_case.exchange.ExchangeOutputBoundary;
import use_case.exchange.ExchangeOutputData;
import use_case.exchange.ExchangeConversionOutputData;

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
        exchangeViewModel.firePropertyChangedRate();
    }
    @Override
    public void presentFailure(String errorMessage) {
        exchangeViewModel.setExchangeRate("Error: " + errorMessage);
        exchangeViewModel.firePropertyChangedRate();
    }

    @Override
    public void presentConversionFailure(String errorMessage) {
        ExchangeState state = exchangeViewModel.getExchangeState();
        state.setErrorMessage(errorMessage);
        state.setConversionMessage("");
        exchangeViewModel.setState(state);
        exchangeViewModel.firePropertyChangedState();
    }

    @Override
    public void presentConversionSuccess(ExchangeConversionOutputData outputData) {
        ExchangeState state = exchangeViewModel.getExchangeState();
        state.setErrorMessage("");

        String msg = String.format(
                "Converted %.2f %s to %.2f %s in '%s' (rate: 1 %s = %.4f %s). " +
                        "New balances: %s: %.2f, %s: %.2f",
                outputData.getAmountGiven(),
                outputData.getFrom(),
                outputData.getAmountReceived(),
                outputData.getTo(),
                outputData.getAccountName(),
                outputData.getFrom(),
                outputData.getRateUsed(),
                outputData.getTo(),
                outputData.getFrom(),
                outputData.getFromBalanceAfter(),
                outputData.getTo(),
                outputData.getToBalanceAfter()
        );

        state.setConversionMessage(msg);
        exchangeViewModel.setState(state);
        exchangeViewModel.firePropertyChangedState();
    }
}