package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

/**
 * High-level coordinator for view configuration.
 * Delegates to smaller configurator classes for each group of views.
 */
public class ViewConfigurator {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private final AuthViewConfigurator authViews;
    private final LoggedInViewConfigurator loggedInViews;
    private final MoneyViewConfigurator moneyViews;
    private final HistoryViewConfigurator historyViews;
    private final AssetViewConfigurator assetViews;

    /**
     * Creates a new {@code ViewConfigurator}.
     *
     * @param cardPanel  the card panel
     * @param cardLayout the card layout
     */
    public ViewConfigurator(final JPanel cardPanel, final CardLayout cardLayout) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;

        this.authViews = new AuthViewConfigurator(cardPanel, cardLayout);
        this.loggedInViews = new LoggedInViewConfigurator(cardPanel, cardLayout);
        this.moneyViews = new MoneyViewConfigurator(cardPanel, cardLayout);
        this.historyViews = new HistoryViewConfigurator(cardPanel, cardLayout);
        this.assetViews = new AssetViewConfigurator(cardPanel, cardLayout);
    }

    /**
     * Creates all views and view models by delegating to sub-configurators.
     */
    public void createViews() {
        authViews.createViews();
        loggedInViews.createViews();
        moneyViews.createViews();
        historyViews.createViews();

        // Inject the LoggedInViewModel into asset views BEFORE they are created
        assetViews.setLoggedInViewModel(loggedInViews.getLoggedInViewModel());
        assetViews.createViews();
    }

    public AuthViewConfigurator getAuthViews() {
        return authViews;
    }

    public LoggedInViewConfigurator getLoggedInViews() {
        return loggedInViews;
    }

    public MoneyViewConfigurator getMoneyViews() {
        return moneyViews;
    }

    public HistoryViewConfigurator getHistoryViews() {
        return historyViews;
    }

    public AssetViewConfigurator getAssetViews() {
        return assetViews;
    }

    /**
     * The initial view to show when the app starts.
     *
     * @return the initial view name
     */
    public String getInitialViewName() {
        return authViews.getInitialViewName();
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }
}
