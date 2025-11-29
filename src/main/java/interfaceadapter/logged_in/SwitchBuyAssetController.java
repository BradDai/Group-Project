package interfaceadapter.logged_in;

import usecase.switch_buyasset.SwitchBuyAssetInputBoundary;

public class SwitchBuyAssetController {

    private final SwitchBuyAssetInputBoundary switchBuyAssetUseCaseInteractor;

    public SwitchBuyAssetController(final SwitchBuyAssetInputBoundary switchBuyAssetUseCaseInteractor) {
        this.switchBuyAssetUseCaseInteractor = switchBuyAssetUseCaseInteractor;
    }
    /**
     * I.
     */

    public void switchToBuyAssetView() {

        switchBuyAssetUseCaseInteractor.switchToBuyAssetView();
    }
}
