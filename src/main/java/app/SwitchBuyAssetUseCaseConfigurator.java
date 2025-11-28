package app;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.SwitchBuyAssetController;
import interfaceadapter.logged_in.SwitchBuyAssetPresenter;
import usecase.switch_buyasset.SwitchBuyAssetInteractor;

/**
 * Wires the "switch to Buy Asset view" use case from the logged-in view.
 */
public class SwitchBuyAssetUseCaseConfigurator {

    private final ViewManagerModel viewManagerModel;
    private final ViewConfigurator views;

    public SwitchBuyAssetUseCaseConfigurator(
            final ViewManagerModel viewManagerModel,
            final ViewConfigurator views
    ) {
        this.viewManagerModel = viewManagerModel;
        this.views = views;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCase() {
        final SwitchBuyAssetPresenter presenter =
                new SwitchBuyAssetPresenter(
                        views.getAssetViews().getBuyAssetViewModel(),
                        viewManagerModel
                );

        final SwitchBuyAssetInteractor interactor =
                new SwitchBuyAssetInteractor(presenter);

        final SwitchBuyAssetController controller =
                new SwitchBuyAssetController(interactor);
        views.getLoggedInViews().getLoggedInView().setSwitchBuyAssetController(controller);
    }
}
