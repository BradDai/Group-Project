package interface_adapter.exchange;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.exchange.ExchangeConversionOutputData;
import use_case.exchange.ExchangeOutputBoundary;
import use_case.exchange.ExchangeOutputData;

public class ExchangePresenter implements ExchangeOutputBoundary {

    private final ExchangeViewModel exchangeViewModel;
    private final LoggedInViewModel loggedInViewModel;

    public ExchangePresenter(final ExchangeViewModel exchangeViewModel, final LoggedInViewModel loggedInViewModel) {
        this.exchangeViewModel = exchangeViewModel;
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void presentSuccess(final ExchangeOutputData outputData) {

        final String formatted = String.format(
            "1 %s = %.4f %s",
            outputData.getFrom(),
            outputData.getRate(),
            outputData.getTo()
        );
        exchangeViewModel.setExchangeRate(formatted);
        exchangeViewModel.firePropertyChangedRate();
    }

    @Override
    public void presentFailure(final String errorMessage) {
        exchangeViewModel.setExchangeRate("Error: " + errorMessage);
        exchangeViewModel.firePropertyChangedRate();
    }

    @Override
    public void presentConversionFailure(final String errorMessage) {
        final ExchangeState state = exchangeViewModel.getExchangeState();
        state.setErrorMessage(errorMessage);
        state.setConversionMessage("");
        exchangeViewModel.setState(state);
        exchangeViewModel.firePropertyChangedState();
    }

    @Override
    public void presentConversionSuccess(final ExchangeConversionOutputData outputData) {
        final ExchangeState state = exchangeViewModel.getExchangeState();
        state.setErrorMessage("");

        final String msg = String.format(
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
        //
        final LoggedInState loggedState = loggedInViewModel.getState();
        loggedState.setSubAccounts(outputData.getUpdatedSubAccounts());
        loggedInViewModel.setState(loggedState);
        loggedInViewModel.firePropertyChange("subAccounts", null, loggedState.getSubAccounts());
    }
}
