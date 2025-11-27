package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

import interfaceadapter.buyasset.BuyAssetViewModel;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.sell_asset.SellAssetViewModel;
import view.BuyAssetView;
import view.SellAssetView;

/**
 * Configures the buy and sell asset views and view models.
 */

public class AssetViewConfigurator {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private LoggedInViewModel loggedInViewModel;

    private BuyAssetViewModel buyAssetViewModel;
    private BuyAssetView buyAssetView;

    private SellAssetViewModel sellAssetViewModel;
    private SellAssetView sellAssetView;

    public AssetViewConfigurator(final JPanel cardPanel, final CardLayout cardLayout) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
    }
    /**
     * Sets the logged-in view model used by the buy-asset view.
     * @param loggedInViewModel The model for log in view.* This must be called before {@link #createViews()}.
     */

    public void setLoggedInViewModel(final LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
    }
    /**
     * Creates views.
     */

    public void createViews() {
        buyAssetViewModel = new BuyAssetViewModel();
        buyAssetView = new BuyAssetView(buyAssetViewModel);

        // loggedInViewModel is injected separately
        buyAssetView.setLoggedInViewModel(loggedInViewModel);

        cardPanel.add(buyAssetView, buyAssetView.getViewName());

        sellAssetViewModel = new SellAssetViewModel();
        sellAssetView = new SellAssetView(sellAssetViewModel);
        cardPanel.add(sellAssetView, sellAssetView.getViewName());
    }

    public BuyAssetViewModel getBuyAssetViewModel() {
        return buyAssetViewModel;
    }

    public BuyAssetView getBuyAssetView() {
        return buyAssetView;
    }

    public SellAssetViewModel getSellAssetViewModel() {
        return sellAssetViewModel;
    }

    public SellAssetView getSellAssetView() {
        return sellAssetView;
    }
}
