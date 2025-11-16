package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.exchange.ExchangeViewModel;
import use_case.switch_exchange.SwitchExchangeOutputBoundary;

public class SwitchExchangePresenter implements SwitchExchangeOutputBoundary {

    private final ExchangeViewModel exchangeViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchExchangePresenter(ExchangeViewModel exchangeViewModel, ViewManagerModel viewManagerModel) {

        this.exchangeViewModel = exchangeViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    public void switchToExchangeView() {

        viewManagerModel.setState(exchangeViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
