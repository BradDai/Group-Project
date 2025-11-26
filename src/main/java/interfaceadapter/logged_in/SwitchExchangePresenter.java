package interfaceadapter.logged_in;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.exchange.ExchangeState;
import interfaceadapter.exchange.ExchangeViewModel;
import usecase.switch_exchange.SwitchExchangeOutputBoundary;

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

        exchangeViewModel.setState(exchangeState);

        viewManagerModel.setState(exchangeViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
