package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

import interfaceadapter.logged_in.LoggedInViewModel;
import view.LoggedInView;

/**
 * Configures the logged-in view and view model.
 */
public class LoggedInViewConfigurator {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;

    public LoggedInViewConfigurator(final JPanel cardPanel, final CardLayout cardLayout) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
    }
    /**
     * Creates views.
     */

    public void createViews() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
    }

    public LoggedInViewModel getLoggedInViewModel() {
        return loggedInViewModel;
    }

    public LoggedInView getLoggedInView() {
        return loggedInView;
    }
}
