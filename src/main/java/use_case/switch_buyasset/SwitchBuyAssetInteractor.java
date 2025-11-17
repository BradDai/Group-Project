package use_case.switch_buyasset;

public class SwitchBuyAssetInteractor implements SwitchBuyAssetInputBoundary {

    private final SwitchBuyAssetOutputBoundary switchBuyAssetPresenter;

    public SwitchBuyAssetInteractor(SwitchBuyAssetOutputBoundary switchBuyAssetOutputBoundary) {
        this.switchBuyAssetPresenter = switchBuyAssetOutputBoundary;
    }

    public void switchToBuyAssetView() { switchBuyAssetPresenter.switchToBuyAssetView(); }
}
