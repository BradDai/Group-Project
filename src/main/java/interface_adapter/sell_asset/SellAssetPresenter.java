package interface_adapter.sell_asset;

import use_case.sell_asset.SellAssetOutputBoundary;
import use_case.sell_asset.SellAssetOutputData;
import use_case.sell_asset.SellAssetPriceOutputBoundary;
import use_case.sell_asset.SellAssetPriceOutputData;

public class SellAssetPresenter implements SellAssetOutputBoundary, SellAssetPriceOutputBoundary {

    private final SellAssetViewModel sellAssetViewModel;

    public SellAssetPresenter(SellAssetViewModel sellAssetViewModel) {
        this.sellAssetViewModel = sellAssetViewModel;
    }

    @Override
    public void prepareSuccessView(SellAssetOutputData sellAssetOutputData) {

    }

    @Override
    public void prepareFailureView(String errorMessage) {

    }

    @Override
    public void preparePriceSuccessView(SellAssetPriceOutputData sellAssetPriceOutputData) {
        SellAssetState sellAssetState = sellAssetViewModel.getState();
        sellAssetState.setCurrentPrice(sellAssetPriceOutputData.getPrice());
        sellAssetState.setPriceError(null);

        sellAssetViewModel.setState(sellAssetState);
        sellAssetViewModel.firePropertyChanged();
    }

    @Override
    public void preparePriceFailureView(String errorMessage) {
        SellAssetState sellAssetState = sellAssetViewModel.getState();
        sellAssetState.setPriceError(errorMessage);
        sellAssetState.setCurrentPrice(0.0);

        sellAssetViewModel.setState(sellAssetState);
        sellAssetViewModel.firePropertyChanged();
    }
}
