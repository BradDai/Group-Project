package interfaceadapter.login;

import usecase.login.LoginInputBoundary;
import usecase.login.LoginInputData;

/**
 * The controller for the Login Use Case.
 */
public class LoginController {

    private final LoginInputBoundary loginUseCaseInteractor;

    public LoginController(final LoginInputBoundary loginUseCaseInteractor) {
        this.loginUseCaseInteractor = loginUseCaseInteractor;
    }

    /**
     * Executes the Login Use Case.
     *
     * @param username the username of the user logging in
     * @param password the password of the user logging in
     */
    public void execute(final String username, final String password) {
        final LoginInputData loginInputData = new LoginInputData(
            username, password);

        loginUseCaseInteractor.execute(loginInputData);
    }

    /**
     * Implements the cancel button in the login view, switching back to the signup
     */
    public void switchToSignupView() {
        loginUseCaseInteractor.switchToSignupView();
    }
}
