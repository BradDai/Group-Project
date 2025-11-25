package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.exchange.ExchangeState;
import interface_adapter.exchange.ExchangeViewModel;
import use_case.switch_exchange.SwitchExchangeOutputBoundary;

public class SwitchExchangePresenter implements SwitchExchangeOutputBoundary {

    private final ExchangeViewModel exchangeViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchExchangePresenter(final ExchangeViewModel exchangeViewModel, final ViewManagerModel viewManagerModel) {
        this.exchangeViewModel = exchangeViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    public void switchToExchangeView(final String username) {
        final ExchangeState exchangeState = exchangeViewModel.getExchangeState();
        exchangeState.setUsername(username);

        exchangeState.setErrorMessage("");
        exchangeState.setConversionMessage("");
        exchangeState.setAmountField("");
        exchangeState.setBalanceMessage("");

        exchangeViewModel.setState(exchangeState);

        viewManagerModel.setState(exchangeViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
