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
        final LoggedInState loggedInState = loggedInViewModel.getState();
        final String name = loggedInState.getUsername();
        loggedInState.setUsername("");
        loggedInState.setSubAccounts(new java.util.ArrayList<>());
        loggedInState.setSubAccountError(null);
        loggedInViewModel.firePropertyChange();

        final LoginState loginState = loginViewModel.getState();
        loginState.setUsername(name);
        loginViewModel.firePropertyChange();

        this.viewManagerModel.setState(loginViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
