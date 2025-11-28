package app;

import dataaccess.FileUserDataAccessObject;
import entity.UserFactory;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.ChangePasswordController;
import interfaceadapter.logged_in.ChangePasswordPresenter;
import interfaceadapter.logout.LogoutController;
import interfaceadapter.logout.LogoutPresenter;
import usecase.change_password.ChangePasswordInputBoundary;
import usecase.change_password.ChangePasswordInteractor;
import usecase.change_password.ChangePasswordOutputBoundary;
import usecase.logout.LogoutInputBoundary;
import usecase.logout.LogoutInteractor;
import usecase.logout.LogoutOutputBoundary;

/**
 * Wires logout and change-password use cases.
 */
public class LogoutChangePasswordUseCaseConfigurator {

    private final ViewManagerModel viewManagerModel;
    private final UserFactory userFactory;
    private final FileUserDataAccessObject userDataAccessObject;
    private final ViewConfigurator views;

    public LogoutChangePasswordUseCaseConfigurator(
            final ViewManagerModel viewManagerModel,
            final UserFactory userFactory,
            final FileUserDataAccessObject userDataAccessObject,
            final ViewConfigurator views
    ) {
        this.viewManagerModel = viewManagerModel;
        this.userFactory = userFactory;
        this.userDataAccessObject = userDataAccessObject;
        this.views = views;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        addLogoutUseCase();
        addChangePasswordUseCase();
    }

    private void addLogoutUseCase() {
        final LogoutOutputBoundary outputBoundary =
                new LogoutPresenter(
                        viewManagerModel,
                        views.getLoggedInViews().getLoggedInViewModel(),
                        views.getAuthViews().getLoginViewModel()
                );

        final LogoutInputBoundary interactor =
                new LogoutInteractor(userDataAccessObject, outputBoundary);

        final LogoutController controller = new LogoutController(interactor);
        views.getLoggedInViews().getLoggedInView().setLogoutController(controller);
    }

    private void addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary outputBoundary =
                new ChangePasswordPresenter(
                        viewManagerModel,
                        views.getLoggedInViews().getLoggedInViewModel()
                );

        final ChangePasswordInputBoundary interactor =
                new ChangePasswordInteractor(
                        userDataAccessObject,
                        outputBoundary,
                        userFactory
                );

        final ChangePasswordController controller =
                new ChangePasswordController(interactor);
        views.getLoggedInViews().getLoggedInView().setChangePasswordController(controller);
    }
}
