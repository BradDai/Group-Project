package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import entity.SubAccount;
import interfaceadapter.SwitchLoggedInController;
import interfaceadapter.buyasset.BuyAssetController;
import interfaceadapter.buyasset.BuyAssetState;
import interfaceadapter.buyasset.BuyAssetViewModel;
import interfaceadapter.buyasset.GetPriceController;
import interfaceadapter.logged_in.LoggedInViewModel;

public class BuyAssetView extends JPanel implements PropertyChangeListener {

    private final BuyAssetViewModel buyAssetViewModel;

    private SwitchLoggedInController switchLoggedInController;
    private GetPriceController getPriceController;
    private BuyAssetController buyAssetController;
    private LoggedInViewModel loggedInViewModel;

    private final JButton back;
    private final JComboBox<String> portfolioComboBox;
    private final JComboBox<String> assetComboBox;
    private final JLabel priceLabel;
    private final JComboBox<Integer> quantityComboBox;
    private final JLabel totalLabel;

    // (Not really needed, but keeping since it was in your original file)
    private static final String API_KEY = "ebcea301f0ad46579daa6b6dea349164";

    public BuyAssetView(final BuyAssetViewModel buyAssetViewModel) {
        this.buyAssetViewModel = buyAssetViewModel;
        this.buyAssetViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // ---- Back button ----
        final JPanel buttons = new JPanel();
        back = new JButton("Back");
        buttons.add(back);
        this.add(buttons);

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                if (evt.getSource().equals(back)) {
                    if (switchLoggedInController != null) {
                        switchLoggedInController.switchToLoggedInView();
                    }
                }
            }
        });

        // ---- Portfolio chooser ----
        final JPanel portfolioPanel = new JPanel();
        portfolioPanel.add(new JLabel("Choose portfolio:"));
        portfolioComboBox = new JComboBox<>();
        // first entry empty (forces explicit choice)
        portfolioComboBox.addItem("");
        portfolioPanel.add(portfolioComboBox);
        this.add(portfolioPanel);

        // ---- Asset chooser ----
        final JPanel assetMenu = new JPanel();
        assetMenu.add(new JLabel("Choose asset:"));
        final String[] assetSymbols = {"", "AAPL", "TSLA", "MSFT", "BTC/USD"};
        assetComboBox = new JComboBox<>(assetSymbols);
        assetMenu.add(assetComboBox);
        this.add(assetMenu);

        // ---- Quantity chooser ----
        final JPanel quantityMenu = new JPanel();
        quantityMenu.add(new JLabel("Quantity:"));
        final Integer[] quantities = {null, 1, 2, 3, 4, 5, 10};
        quantityComboBox = new JComboBox<>(quantities);
        quantityMenu.add(quantityComboBox);
        this.add(quantityMenu);

        // ---- Price / total labels ----
        priceLabel = new JLabel("Price: -");
        this.add(priceLabel);

        totalLabel = new JLabel("Total: -");
        this.add(totalLabel);

        // ---- Purchase button ----
        final JButton purchaseButton = new JButton("Purchase");
        this.add(purchaseButton);

        // ---- Listeners ----
        assetComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                onAssetSelected();
            }
        });

        quantityComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                onQuantitySelected();
            }
        });

        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                onPurchase();
            }
        });
    }

    // ================== Internal helpers ==================

    private void onAssetSelected() {
        final String symbol = (String) assetComboBox.getSelectedItem();

        final BuyAssetState state = buyAssetViewModel.getState();
        state.selectedSymbol = (symbol == null) ? "" : symbol;
        buyAssetViewModel.setState(state);

        if (getPriceController != null && symbol != null && !symbol.isEmpty()) {
            getPriceController.execute(symbol);
        }

        System.out.println("Selected asset = " + symbol);
    }

    private void onQuantitySelected() {
        final Integer qty = (Integer) quantityComboBox.getSelectedItem();
        final BuyAssetState state = buyAssetViewModel.getState();
        state.selectedQuantity = qty;

        if (qty != null && state.price > 0) {
            state.total = state.price * qty;
        }
        else {
            state.total = 0.0;
        }

        buyAssetViewModel.setState(state);
        buyAssetViewModel.firePropertyChange();
    }

    private void onPurchase() {
        final BuyAssetState state = buyAssetViewModel.getState();
        final String symbol = state.selectedSymbol;
        final Integer qty = state.selectedQuantity;
        final String portfolioName = (String) portfolioComboBox.getSelectedItem();
        final String username = (loggedInViewModel != null)
            ? loggedInViewModel.getState().getUsername()
            : null;

        if (buyAssetController == null) {
            JOptionPane.showMessageDialog(this, "Buy controller not set.");
            return;
        }

        // ---- Basic validation mirroring your interactor checks ----
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No user logged in.");
            return;
        }

        if (portfolioName == null || portfolioName.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please choose a portfolio.");
            return;
        }

        if (symbol == null || symbol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please choose an asset.");
            return;
        }

        if (qty == null || qty <= 0) {
            JOptionPane.showMessageDialog(this, "Please choose a valid quantity.");
            return;
        }

        if (state.price <= 0) {
            JOptionPane.showMessageDialog(this, "Price not loaded yet.");
            return;
        }

        // ---- Call interactor through controller ----
        buyAssetController.execute(
            username,
            portfolioName,
            symbol,
            qty,
            state.price
        );
    }

    private void refreshPortfolios() {
        if (loggedInViewModel == null) {
            return;
        }

        portfolioComboBox.removeAllItems();
        portfolioComboBox.addItem(""); // empty first option

        final List<SubAccount> accounts = loggedInViewModel.getState().getSubAccounts();
        if (accounts != null) {
            for (final SubAccount sa : accounts) {
                portfolioComboBox.addItem(sa.getName());
            }
        }
    }

    // ================== PropertyChangeListener ==================

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final BuyAssetState state = buyAssetViewModel.getState();

        if (state.purchaseMessage != null) {
            JOptionPane.showMessageDialog(this, state.purchaseMessage);
            state.purchaseMessage = null;
        }

        if (state.errorMessage != null) {
            JOptionPane.showMessageDialog(this, state.errorMessage);
            state.errorMessage = null;
        }

        priceLabel.setText(state.price > 0 ? "Price: " + state.price : "Price: -");
        totalLabel.setText(state.total > 0 ? "Total: " + state.total : "Total: -");
    }

    // ================== Wiring methods ==================

    public void setGetPriceController(final GetPriceController controller) {
        this.getPriceController = controller;
    }

    public void setBuyAssetController(final BuyAssetController controller) {
        this.buyAssetController = controller;
    }

    public void setLoggedInViewModel(final LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;

        if (this.loggedInViewModel != null) {
            this.loggedInViewModel.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    refreshPortfolios();
                }
            });
        }

        refreshPortfolios();
    }

    public void setSwitchLoggedInController(final SwitchLoggedInController controller) {
        this.switchLoggedInController = controller;
    }

    public String getViewName() {
        final String viewName = "buyasset";
        return viewName;
    }
}
