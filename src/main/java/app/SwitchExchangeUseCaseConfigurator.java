package app;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.SwitchExchangeController;
import interfaceadapter.logged_in.SwitchExchangePresenter;
import usecase.switch_exchange.SwitchExchangeInteractor;

/**
 * Wires the "switch to Exchange view" use case from the logged-in view.
 */
public class SwitchExchangeUseCaseConfigurator {

    private final ViewManagerModel viewManagerModel;
    private final ViewConfigurator views;

    public SwitchExchangeUseCaseConfigurator(
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
        // Presenter
        SwitchExchangePresenter presenter =
                new SwitchExchangePresenter(
                        views.getMoneyViews().getExchangeViewModel(),
                        viewManagerModel
                );

        // Interactor (we don't need to mention the interface type here)
        SwitchExchangeInteractor interactor =
                new SwitchExchangeInteractor(presenter);

        // Controller
        SwitchExchangeController controller =
                new SwitchExchangeController(interactor);

        views.getLoggedInViews().getLoggedInView().setSwitchExchangeController(controller);
    }
}
