package interfaceadapter.logged_in;

import usecase.switch_sellasset.SwitchSellAssetInputBoundary;

public class SwitchSellAssetController {

    private final SwitchSellAssetInputBoundary switchSellAssetUseCaseInteractor;

    public SwitchSellAssetController(final SwitchSellAssetInputBoundary switchSellAssetUseCaseInteractor) {
        this.switchSellAssetUseCaseInteractor = switchSellAssetUseCaseInteractor;
    }

    /**
     * Method to switch to sell asset view.
     *
     * @param username the username
     */
    public void switchToSellAssetView(final String username) {

        switchSellAssetUseCaseInteractor.switchToSellAssetView(username);
    }
}
