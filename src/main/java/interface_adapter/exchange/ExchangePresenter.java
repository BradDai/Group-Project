package interface_adapter.exchange;

import interface_adapter.ViewManagerModel;
import use_case.exchange.ExchangeOutputBoundary;

public class ExchangePresenter implements ExchangeOutputBoundary {

    private final ExchangeViewModel exchangeViewModel;
    private final ViewManagerModel viewManagerModel;

    public ExchangePresenter(ExchangeViewModel exchangeViewModel, ViewManagerModel viewManagerModel) {
        this.exchangeViewModel = exchangeViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView() {
        System.out.println("oe");
        this.viewManagerModel.setState(exchangeViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
