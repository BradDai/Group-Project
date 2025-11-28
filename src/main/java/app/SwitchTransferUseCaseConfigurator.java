package app;

import dataaccess.FileSubAccountDataAccessJSON;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.SwitchTransferController;
import interfaceadapter.logged_in.SwitchTransferPresenter;
import usecase.switch_transfer.SwitchTransferInteractor;

/**
 * Wires the "switch to Transfer view" use case from the logged-in view.
 */
public class SwitchTransferUseCaseConfigurator {

    private final ViewManagerModel viewManagerModel;
    private final FileSubAccountDataAccessJSON subAccountDataAccess;
    private final ViewConfigurator views;

    public SwitchTransferUseCaseConfigurator(
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
        final SwitchTransferPresenter presenter =
                new SwitchTransferPresenter(
                        views.getMoneyViews().getTransferViewModel(),
                        viewManagerModel
                );

        final SwitchTransferInteractor interactor =
                new SwitchTransferInteractor(
                        presenter,
                        subAccountDataAccess
                );

        final SwitchTransferController controller =
                new SwitchTransferController(interactor);
        views.getLoggedInViews().getLoggedInView().setSwitchTransferController(controller);
    }
}
