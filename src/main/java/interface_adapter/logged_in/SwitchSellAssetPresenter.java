package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.sellasset.SellAssetViewModel;
import use_case.switch_sellasset.SwitchSellAssetOutputBoundary;

public class SwitchSellAssetPresenter implements SwitchSellAssetOutputBoundary {

    private final SellAssetViewModel sellAssetViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchSellAssetPresenter(SellAssetViewModel sellAssetViewModel, ViewManagerModel viewManagerModel) {

        this.sellAssetViewModel = sellAssetViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    public void switchToSellAssetView() {

        viewManagerModel.setState(sellAssetViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
