package usecase.switch_buyasset;

public class SwitchBuyAssetInteractor implements SwitchBuyAssetInputBoundary {

    private final SwitchBuyAssetOutputBoundary switchBuyAssetPresenter;

    public SwitchBuyAssetInteractor(final SwitchBuyAssetOutputBoundary switchBuyAssetOutputBoundary) {
        this.switchBuyAssetPresenter = switchBuyAssetOutputBoundary;
    }

    /**
     * I.
     */
    public void switchToBuyAssetView() {
        switchBuyAssetPresenter.switchToBuyAssetView();
    }
}
