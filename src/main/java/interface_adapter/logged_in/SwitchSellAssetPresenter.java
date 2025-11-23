package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.sell_asset.SellAssetState;
import interface_adapter.sell_asset.SellAssetViewModel;
import use_case.switch_sellasset.SwitchSellAssetOutputBoundary;

public class SwitchSellAssetPresenter implements SwitchSellAssetOutputBoundary {

    private final SellAssetViewModel sellAssetViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchSellAssetPresenter(SellAssetViewModel sellAssetViewModel, ViewManagerModel viewManagerModel) {
        this.sellAssetViewModel = sellAssetViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    public void switchToSellAssetView(String username, String[] portfolios) {
        SellAssetState state = sellAssetViewModel.getState();
        state.setUsername(username);
        state.setPortfolios(portfolios);
        state.setErrorMessage("");

        viewManagerModel.setState(sellAssetViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
