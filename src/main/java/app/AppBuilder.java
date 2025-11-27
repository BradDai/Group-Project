package app;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import interfaceadapter.ViewManagerModel;
import view.ViewManager;

/**
 * Thin builder for the main application.
 *
 * <p>
 * It creates the Swing container and view manager model,
 * then delegates all wiring to {@link ApplicationConfigurator}.
 */
public class AppBuilder {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private final ViewManagerModel viewManagerModel;
    private final ApplicationConfigurator configurator;
    // Keep a reference so the listener stays alive.
    private final ViewManager viewManager;

    /**
     * Creates a new {@code AppBuilder} with an empty card layout.
     */
    public AppBuilder() {
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        this.viewManagerModel = new ViewManagerModel();

        // Create the view manager that listens to ViewManagerModel
        // and switches cards accordingly.
        this.viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

        // Delegate heavy wiring to configurator.
        this.configurator = new ApplicationConfigurator(
                cardPanel,
                cardLayout,
                viewManagerModel
        );
    }

    /**
     * Builds and returns the main application frame.
     *
     * @return the configured {@link JFrame}
     */
    public JFrame build() {
        // Set up all views and use cases.
        configurator.configure();

        JFrame application = new JFrame("Banking Simulation");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);

        // Start on the initial view (signup).
        viewManagerModel.setState(configurator.getInitialViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}
