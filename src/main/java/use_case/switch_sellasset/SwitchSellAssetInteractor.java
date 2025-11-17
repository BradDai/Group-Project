package use_case.switch_sellasset;

public class SwitchSellAssetInteractor implements SwitchSellAssetInputBoundary {

    private final SwitchSellAssetOutputBoundary sellAssetPresenter;

    public SwitchSellAssetInteractor(SwitchSellAssetOutputBoundary sellAssetOutputBoundary) {
        this.sellAssetPresenter = sellAssetOutputBoundary;
    }

    public void switchToSellAssetView() { sellAssetPresenter.switchToSellAssetView(); }
}
