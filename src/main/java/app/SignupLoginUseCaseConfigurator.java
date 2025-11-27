package app;

import dataaccess.FileSubAccountDataAccessJSON;
import dataaccess.FileUserDataAccessObject;
import entity.UserFactory;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.login.LoginController;
import interfaceadapter.login.LoginPresenter;
import interfaceadapter.signup.SignupController;
import interfaceadapter.signup.SignupPresenter;
import usecase.login.LoginInputBoundary;
import usecase.login.LoginInteractor;
import usecase.login.LoginOutputBoundary;
import usecase.signup.SignupInputBoundary;
import usecase.signup.SignupInteractor;
import usecase.signup.SignupOutputBoundary;

/**
 * Wires signup and login use cases.
 */
public class SignupLoginUseCaseConfigurator {

    private final ViewManagerModel viewManagerModel;
    private final UserFactory userFactory;
    private final FileUserDataAccessObject userDataAccessObject;
    private final FileSubAccountDataAccessJSON subAccountDataAccess;
    private final ViewConfigurator views;

    public SignupLoginUseCaseConfigurator(
            final ViewManagerModel viewManagerModel,
            final UserFactory userFactory,
            final FileUserDataAccessObject userDataAccessObject,
            final FileSubAccountDataAccessJSON subAccountDataAccess,
            final ViewConfigurator views
    ) {
        this.viewManagerModel = viewManagerModel;
        this.userFactory = userFactory;
        this.userDataAccessObject = userDataAccessObject;
        this.subAccountDataAccess = subAccountDataAccess;
        this.views = views;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        addSignupUseCase();
        addLoginUseCase();
    }

    private void addSignupUseCase() {
        SignupOutputBoundary outputBoundary =
                new SignupPresenter(
                        viewManagerModel,
                        views.getAuthViews().getSignupViewModel(),
                        views.getAuthViews().getLoginViewModel()
                );

        SignupInputBoundary interactor =
                new SignupInteractor(
                        userDataAccessObject,
                        outputBoundary,
                        userFactory,
                        subAccountDataAccess
                );

        SignupController controller = new SignupController(interactor);
        views.getAuthViews().getSignupView().setSignupController(controller);
    }

    private void addLoginUseCase() {
        LoginOutputBoundary outputBoundary =
                new LoginPresenter(
                        viewManagerModel,
                        views.getLoggedInViews().getLoggedInViewModel(),
                        views.getAuthViews().getLoginViewModel(),
                        views.getAuthViews().getSignupViewModel()
                );

        LoginInputBoundary interactor =
                new LoginInteractor(
                        userDataAccessObject,
                        outputBoundary,
                        subAccountDataAccess
                );

        LoginController controller = new LoginController(interactor);
        views.getAuthViews().getLoginView().setLoginController(controller);
    }
}
