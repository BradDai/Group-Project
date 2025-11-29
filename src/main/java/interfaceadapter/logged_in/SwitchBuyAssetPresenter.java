package interfaceadapter.logged_in;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.buyasset.BuyAssetViewModel;
import usecase.switch_buyasset.SwitchBuyAssetOutputBoundary;

public class SwitchBuyAssetPresenter implements SwitchBuyAssetOutputBoundary {

    private final BuyAssetViewModel buyAssetViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchBuyAssetPresenter(final BuyAssetViewModel buyAssetViewModel, final ViewManagerModel viewManagerModel) {

        this.buyAssetViewModel = buyAssetViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * I.
     */
    public void switchToBuyAssetView() {

        viewManagerModel.setState(buyAssetViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
