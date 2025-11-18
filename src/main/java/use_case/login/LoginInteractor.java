package use_case.login;

import entity.SubAccount;
import entity.User;
import use_case.SubAccount.SubAccountDataAccessInterface;

import java.util.List;

/**
 * The Login Interactor.
 */
public class LoginInteractor implements LoginInputBoundary {
    private final LoginUserDataAccessInterface userDataAccessObject;
    private final LoginOutputBoundary loginPresenter;
    private final SubAccountDataAccessInterface subAccountDataAccess;

    public LoginInteractor(LoginUserDataAccessInterface userDataAccessInterface,
                           LoginOutputBoundary loginOutputBoundary,
                           SubAccountDataAccessInterface subAccountDataAccess) {
        this.userDataAccessObject = userDataAccessInterface;
        this.loginPresenter = loginOutputBoundary;
        this.subAccountDataAccess = subAccountDataAccess;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        final String username = loginInputData.getUsername();
        final String password = loginInputData.getPassword();
        if (!userDataAccessObject.existsByName(username)) {
            loginPresenter.prepareFailView(username + ": Account does not exist.");
        }
        else {
            final String pwd = userDataAccessObject.get(username).getPassword();
            if (!password.equals(pwd)) {
                loginPresenter.prepareFailView("Incorrect password for \"" + username + "\".");
            }
            else {

                final User user = userDataAccessObject.get(loginInputData.getUsername());

                userDataAccessObject.setCurrentUsername(username);

                final List<SubAccount> subs = subAccountDataAccess.getSubAccountsOf(username);
                final LoginOutputData loginOutputData =
                        new LoginOutputData(user.getName(), subs);
                loginPresenter.prepareSuccessView(loginOutputData);
            }
        }
    }

    @Override
    public void switchToSignupView() {
        loginPresenter.switchToSignupView();
    }
}