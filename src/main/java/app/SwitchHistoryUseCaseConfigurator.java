package app;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.SwitchHistoryController;
import interfaceadapter.logged_in.SwitchHistoryPresenter;
import usecase.switch_history.SwitchHistoryInteractor;

/**
 * Wires the "switch to History view" use case from the logged-in view.
 */
public class SwitchHistoryUseCaseConfigurator {

    private final ViewManagerModel viewManagerModel;
    private final ViewConfigurator views;

    public SwitchHistoryUseCaseConfigurator(
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
        SwitchHistoryPresenter presenter =
                new SwitchHistoryPresenter(
                        views.getHistoryViews().getHistoryViewModel(),
                        viewManagerModel
                );

        SwitchHistoryInteractor interactor =
                new SwitchHistoryInteractor(presenter);

        SwitchHistoryController controller =
                new SwitchHistoryController(interactor);
        views.getLoggedInViews().getLoggedInView().setSwitchHistoryController(controller);
    }
}
