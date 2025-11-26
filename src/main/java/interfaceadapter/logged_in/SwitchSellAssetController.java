package interfaceadapter.logged_in;

import usecase.switch_sellasset.SwitchSellAssetInputBoundary;

public class SwitchSellAssetController {

    private final SwitchSellAssetInputBoundary switchSellAssetUseCaseInteractor;

    public SwitchSellAssetController(final SwitchSellAssetInputBoundary switchSellAssetUseCaseInteractor) {
        this.switchSellAssetUseCaseInteractor = switchSellAssetUseCaseInteractor;
    }

    public void switchToSellAssetView(final String username) {

        switchSellAssetUseCaseInteractor.switchToSellAssetView(username);
    }
}
