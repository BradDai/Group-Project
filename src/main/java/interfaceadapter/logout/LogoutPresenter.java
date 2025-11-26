package interfaceadapter.logout;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.login.LoginState;
import interfaceadapter.login.LoginViewModel;
import usecase.logout.LogoutOutputBoundary;
import usecase.logout.LogoutOutputData;

/**
 * The Presenter for the Logout Use Case.
 */
public class LogoutPresenter implements LogoutOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoginViewModel loginViewModel;

    public LogoutPresenter(final ViewManagerModel viewManagerModel,
                           final LoggedInViewModel loggedInViewModel,
                           final LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(final LogoutOutputData response) {

        // We need to switch to the login view, which should have
        // an empty username and password.

        // We also need to set the username in the LoggedInState to
        // the empty string.

        // 1. get the LoggedInState out of the appropriate View Model,
        // 2. set the username in the state to the empty string
        // 3. firePropertyChanged so that the View that is listening is updated.
        final LoggedInState loggedInState = loggedInViewModel.getState();
        final String name = loggedInState.getUsername();
        loggedInState.setUsername("");
        loggedInState.setSubAccounts(new java.util.ArrayList<>()); // ⭐ 清空 subAccounts
        loggedInState.setSubAccountError(null);                    // ⭐ 清空错误信息（可选）
        loggedInViewModel.firePropertyChange();
        // 1. get the LoginState out of the appropriate View Model,
        // 2. set the username in the state to be the username of the user that just logged out,
        // 3. firePropertyChanged so that the View that is listening is updated.
        final LoginState loginState = loginViewModel.getState();
        loginState.setUsername(name);
        loginViewModel.firePropertyChange();


        // This code tells the View Manager to switch to the LoginView.
        this.viewManagerModel.setState(loginViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
