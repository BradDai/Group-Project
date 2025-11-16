package interface_adapter.logged_in;

import use_case.switch_buyasset.SwitchBuyAssetInputBoundary;

public class SwitchBuyAssetController {

    private final SwitchBuyAssetInputBoundary switchBuyAssetUseCaseInteractor;

    public SwitchBuyAssetController(SwitchBuyAssetInputBoundary switchBuyAssetUseCaseInteractor) {
        this.switchBuyAssetUseCaseInteractor = switchBuyAssetUseCaseInteractor;
    }

    public void switchToBuyAssetView() {

        switchBuyAssetUseCaseInteractor.switchToBuyAssetView();
    }
}
