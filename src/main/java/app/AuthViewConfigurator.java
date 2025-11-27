package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

import interfaceadapter.login.LoginViewModel;
import interfaceadapter.signup.SignupViewModel;
import view.LoginView;
import view.SignupView;

/**
 * Configures the signup and login views and view models.
 */
public class AuthViewConfigurator {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private SignupViewModel signupViewModel;
    private SignupView signupView;

    private LoginViewModel loginViewModel;
    private LoginView loginView;

    public AuthViewConfigurator(final JPanel cardPanel, final CardLayout cardLayout) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
    }

    /**
     * Creates and registers the signup and login views.
     */
    public void createViews() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());

        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
    }

    public SignupViewModel getSignupViewModel() {
        return signupViewModel;
    }

    public SignupView getSignupView() {
        return signupView;
    }

    public LoginViewModel getLoginViewModel() {
        return loginViewModel;
    }

    public LoginView getLoginView() {
        return loginView;
    }

    /**
     * Initial view name for the application (signup).
     *
     * @return the signup view name
     */
    public String getInitialViewName() {
        return signupView.getViewName();
    }
}
