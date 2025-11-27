package app;

import dataaccess.FileSubAccountDataAccessJSON;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.SwitchSellAssetController;
import interfaceadapter.logged_in.SwitchSellAssetPresenter;
import usecase.switch_sellasset.SwitchSellAssetInteractor;

/**
 * Wires the "switch to Sell Asset view" use case from the logged-in view.
 */
public class SwitchSellAssetUseCaseConfigurator {

    private final ViewManagerModel viewManagerModel;
    private final FileSubAccountDataAccessJSON subAccountDataAccess;
    private final ViewConfigurator views;

    public SwitchSellAssetUseCaseConfigurator(
            final ViewManagerModel viewManagerModel,
            final FileSubAccountDataAccessJSON subAccountDataAccess,
            final ViewConfigurator views
    ) {
        this.viewManagerModel = viewManagerModel;
        this.subAccountDataAccess = subAccountDataAccess;
        this.views = views;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCase() {
        SwitchSellAssetPresenter presenter =
                new SwitchSellAssetPresenter(
                        views.getAssetViews().getSellAssetViewModel(),
                        viewManagerModel
                );

        SwitchSellAssetInteractor interactor =
                new SwitchSellAssetInteractor(
                        presenter,
                        subAccountDataAccess
                );

        SwitchSellAssetController controller =
                new SwitchSellAssetController(interactor);
        views.getLoggedInViews().getLoggedInView().setSwitchSellAssetController(controller);
    }
}
