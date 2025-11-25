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
import javax.swing.JTextField;

import entity.SubAccount;
import interface_adapter.SwitchLoggedInController;
import interface_adapter.buyasset.BuyAssetController;
import interface_adapter.buyasset.BuyAssetState;
import interface_adapter.buyasset.BuyAssetViewModel;
import interface_adapter.buyasset.GetPriceController;
import interface_adapter.logged_in.LoggedInViewModel;

public class BuyAssetView extends JPanel implements PropertyChangeListener {

    private final String viewName = "buyasset";
    private final BuyAssetViewModel buyAssetViewModel;
    private SwitchLoggedInController switchLoggedInController;
    private GetPriceController getPriceController;
    private BuyAssetController buyAssetController;
    private LoggedInViewModel loggedInViewModel;
    private final JButton back;
    private final JComboBox<String> portfolioComboBox;
    private final JComboBox<String> assetComboBox;
    private final JLabel priceLabel;
    private final JTextField quantityField;
    private final JLabel totalLabel;
    private final JButton purchaseButton;

    // Lawrence's key
    private static final String API_KEY = "ebcea301f0ad46579daa6b6dea349164";

    public BuyAssetView(final BuyAssetViewModel buyAssetViewModel) {
        this.buyAssetViewModel = buyAssetViewModel;
        this.buyAssetViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

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

        final JPanel portfolioPanel = new JPanel();
        portfolioPanel.add(new JLabel("Choose portfolio:"));
        portfolioComboBox = new JComboBox<>();
        portfolioComboBox.addItem("");
        portfolioPanel.add(portfolioComboBox);
        this.add(portfolioPanel);

        final JPanel assetMenu = new JPanel();
        assetMenu.add(new JLabel("Choose asset:"));
        String[] assetSymbols = {"",
                "AAPL", "MSFT", "TSLA", "AMZN", "GOOGL", "NVDA", "META", "NFLX", "AMD", "INTC", "BABA", "SHOP", "UBER",
                "SPY", "QQQ", "VTI", "BTC/USD", "ETH/USD"};
        assetComboBox = new JComboBox<>(assetSymbols);
        assetMenu.add(assetComboBox);
        this.add(assetMenu);

        final JPanel quantityMenu = new JPanel();
        quantityMenu.add(new JLabel("Quantity:"));
        quantityField = new JTextField(8);
        quantityMenu.add(quantityField);
        this.add(quantityMenu);

        priceLabel = new JLabel("Price: -");
        this.add(priceLabel);

        totalLabel = new JLabel("Total: -");
        this.add(totalLabel);

        purchaseButton = new JButton("Purchase");
        this.add(purchaseButton);

        assetComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                onAssetSelected();
            }
        });
        quantityField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onQuantitySelected();
            }
        });

        quantityField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {onQuantitySelected();}
            public void removeUpdate(javax.swing.event.DocumentEvent e) {onQuantitySelected();}
            public void changedUpdate(javax.swing.event.DocumentEvent e) {onQuantitySelected();}
        });

        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                onPurchase();
            }
        });
    }

    private void onAssetSelected() {
        final String symbol = (String) assetComboBox.getSelectedItem();

        final BuyAssetState state = buyAssetViewModel.getState();
        if (symbol == null) {
            state.selectedSymbol = "";
        }
        else {
            state.selectedSymbol = symbol;
        }
        buyAssetViewModel.setState(state);

        if (getPriceController != null && symbol != null && !symbol.isEmpty()) {
            getPriceController.execute(symbol);
        }
        System.out.println("Selected asset = " + symbol);

    }

    private void onQuantitySelected() {
        BuyAssetState state = buyAssetViewModel.getState();

        String text = quantityField.getText().trim();
        Integer qty = null;

        if (!text.isEmpty()) {
            try {
                qty = Integer.parseInt(text);}
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantity must be an integer.");
                quantityField.setText("");}
        }
        state.selectedQuantity = qty;
        if (qty != null && qty > 0 && state.price > 0) {
            state.total = state.price * qty;}
        else {state.total = 0.0;}
        buyAssetViewModel.setState(state);
        buyAssetViewModel.firePropertyChange();
    }

    private void onPurchase() {
        onQuantitySelected();
        BuyAssetState state = buyAssetViewModel.getState();
        String symbol = state.selectedSymbol;
        Integer qty = state.selectedQuantity;
        String portfolio = (String) portfolioComboBox.getSelectedItem();
        String username = loggedInViewModel.getState().getUsername();
        if (symbol == null || symbol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please choose an asset.");
            return;}
        if (qty == null || qty <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
            return;}
        if (state.price <= 0) {
            JOptionPane.showMessageDialog(this, "Price not loaded.");
            return;}
        if (portfolio == null || portfolio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please choose a portfolio.");
            return;}
        if (buyAssetController == null) {
            JOptionPane.showMessageDialog(this, "BuyAssetController not set.");
            return;}
        buyAssetController.execute(
                username,
                portfolio,
                symbol,
                qty,
                state.price
        );
    }

    private void refreshPortfolios() {
        if (loggedInViewModel != null) {
            portfolioComboBox.removeAllItems();
            portfolioComboBox.addItem("");
            final List<SubAccount> accounts = loggedInViewModel.getState().getSubAccounts();
            for (final SubAccount sa : accounts) {
                portfolioComboBox.addItem(sa.getName());
            }
        }

    }

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
        if (state.price > 0) {
            priceLabel.setText("Price: " + state.price);
        }
        else {
            priceLabel.setText("Price: -");
        }
        if (state.total > 0) {
            totalLabel.setText("Total: " + state.total);
        }
        else {
            totalLabel.setText("Total: -");
        }
    }

    public void setGetPriceController(final GetPriceController controller) {
        this.getPriceController = controller;
    }

    public void setBuyAssetController(final BuyAssetController controller) {
        this.buyAssetController = controller;
    }

    public void setLoggedInViewModel(final LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                refreshPortfolios();
            }
        });
        refreshPortfolios();
    }

    public void setSwitchLoggedInController(final SwitchLoggedInController controller) {
        this.switchLoggedInController = controller;
    }

    public String getViewName() {
        return viewName;
    }
}
