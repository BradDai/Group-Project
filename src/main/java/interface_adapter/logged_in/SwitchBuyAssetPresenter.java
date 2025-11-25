package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.buyasset.BuyAssetViewModel;
import use_case.switch_buyasset.SwitchBuyAssetOutputBoundary;

public class SwitchBuyAssetPresenter implements SwitchBuyAssetOutputBoundary {

    private final BuyAssetViewModel buyAssetViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchBuyAssetPresenter(final BuyAssetViewModel buyAssetViewModel, final ViewManagerModel viewManagerModel) {

        this.buyAssetViewModel = buyAssetViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    public void switchToBuyAssetView() {

        viewManagerModel.setState(buyAssetViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
