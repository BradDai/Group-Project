package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

import interfaceadapter.ViewManagerModel;

/**
 * High-level coordinator: sets layout, creates views, and delegates
 * use case wiring to {@link UseCaseBootstrapper}.
 */
public class ApplicationConfigurator {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private final ViewManagerModel viewManagerModel;

    private final InfrastructureConfig infra;
    private final ViewConfigurator viewConfigurator;
    private final UseCaseBootstrapper useCaseBootstrapper;

    /**
     * Creates a new configurator bound to the given Swing infrastructure.
     *
     * @param cardPanel        the card panel
     * @param cardLayout       the card layout
     * @param viewManagerModel the view manager model
     */
    public ApplicationConfigurator(
            final JPanel cardPanel,
            final CardLayout cardLayout,
            final ViewManagerModel viewManagerModel
    ) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        this.viewManagerModel = viewManagerModel;

        this.infra = new InfrastructureConfig();
        this.viewConfigurator = new ViewConfigurator(cardPanel, cardLayout);
        this.useCaseBootstrapper =
                new UseCaseBootstrapper(viewManagerModel, infra, viewConfigurator);
    }

    /**
     * Configures all views and use cases.
     */
    public void configure() {
        cardPanel.setLayout(cardLayout);
        viewConfigurator.createViews();
        useCaseBootstrapper.wireAllUseCases();
    }

    /**
     * Returns the initial view name (signup view).
     *
     * @return the initial view name
     */
    public String getInitialViewName() {
        return viewConfigurator.getInitialViewName();
    }
}
