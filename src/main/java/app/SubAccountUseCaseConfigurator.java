package app;

import dataaccess.FileSubAccountDataAccessJSON;
import interfaceadapter.subaccount.create.CreateSubAccountController;
import interfaceadapter.subaccount.create.CreateSubAccountPresenter;
import interfaceadapter.subaccount.delete.DeleteSubAccountController;
import interfaceadapter.subaccount.delete.DeleteSubAccountPresenter;
import usecase.SubAccount.create.CreateSubAccountInputBoundary;
import usecase.SubAccount.create.CreateSubAccountInteractor;
import usecase.SubAccount.create.CreateSubAccountOutputBoundary;
import usecase.SubAccount.delete.DeleteSubAccountInputBoundary;
import usecase.SubAccount.delete.DeleteSubAccountInteractor;
import usecase.SubAccount.delete.DeleteSubAccountOutputBoundary;

/**
 * Wires create/delete subaccount use cases.
 */
public class SubAccountUseCaseConfigurator {

    private final FileSubAccountDataAccessJSON subAccountDataAccess;
    private final ViewConfigurator views;

    public SubAccountUseCaseConfigurator(
            final FileSubAccountDataAccessJSON subAccountDataAccess,
            final ViewConfigurator views
    ) {
        this.subAccountDataAccess = subAccountDataAccess;
        this.views = views;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        addCreateSubAccountUseCase();
        addDeleteSubAccountUseCase();
    }

    private void addCreateSubAccountUseCase() {
        final CreateSubAccountOutputBoundary outputBoundary =
                new CreateSubAccountPresenter(
                        views.getLoggedInViews().getLoggedInViewModel()
                );

        final CreateSubAccountInputBoundary interactor =
                new CreateSubAccountInteractor(
                        subAccountDataAccess,
                        outputBoundary
                );

        final CreateSubAccountController controller =
                new CreateSubAccountController(interactor);
        views.getLoggedInViews().getLoggedInView().setCreateSubAccountController(controller);
    }

    private void addDeleteSubAccountUseCase() {
        final DeleteSubAccountOutputBoundary presenter =
                new DeleteSubAccountPresenter(
                        views.getLoggedInViews().getLoggedInViewModel()
                );

        final DeleteSubAccountInputBoundary interactor =
                new DeleteSubAccountInteractor(
                        subAccountDataAccess,
                        presenter
                );

        final DeleteSubAccountController controller =
                new DeleteSubAccountController(interactor);
        views.getLoggedInViews().getLoggedInView().setDeleteSubAccountController(controller);
    }
}
