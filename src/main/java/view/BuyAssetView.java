package view;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.buyasset.BuyAssetController;
import interface_adapter.buyasset.BuyAssetState;
import interface_adapter.buyasset.BuyAssetViewModel;
import interface_adapter.buyasset.GetPriceController;
import interface_adapter.logged_in.LoggedInViewModel;
import entity.SubAccount;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

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

    public BuyAssetView(BuyAssetViewModel buyAssetViewModel) {
        this.buyAssetViewModel = buyAssetViewModel;
        this.buyAssetViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel buttons = new JPanel();
        back = new JButton("Back");
        buttons.add(back);
        this.add(buttons);

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(back)) {
                    if (switchLoggedInController != null) {
                        switchLoggedInController.switchToLoggedInView();}}
            }
        });

        JPanel portfolioPanel = new JPanel();
        portfolioPanel.add(new JLabel("Choose portfolio:"));
        portfolioComboBox = new JComboBox<>();
        portfolioComboBox.addItem("");
        portfolioPanel.add(portfolioComboBox);
        this.add(portfolioPanel);

        JPanel assetMenu = new JPanel();
        assetMenu.add(new JLabel("Choose asset:"));
        String[] assetSymbols = {"",
            "AAPL", "MSFT", "TSLA", "AMZN", "GOOGL", "NVDA", "META", "NFLX", "AMD", "INTC", "BABA", "SHOP", "UBER",
            "SPY", "QQQ", "VTI", "BTC/USD", "ETH/USD"};
        assetComboBox = new JComboBox<>(assetSymbols);
        assetMenu.add(assetComboBox);
        this.add(assetMenu);

        JPanel quantityMenu = new JPanel();
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
            public void actionPerformed(ActionEvent e) {
                onAssetSelected();}
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
            public void actionPerformed(ActionEvent e) {
                onPurchase();
            }
        });
    }

    private void onAssetSelected() {
        String symbol = (String) assetComboBox.getSelectedItem();

        BuyAssetState state = buyAssetViewModel.getState();
        state.selectedSymbol = (symbol == null) ? "" : symbol;
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
        if (loggedInViewModel == null) {
            return;}

        portfolioComboBox.removeAllItems();
        portfolioComboBox.addItem("");

        List<SubAccount> accounts = loggedInViewModel.getState().getSubAccounts();
        for (SubAccount sa : accounts) {
            portfolioComboBox.addItem(sa.getName());}
    }


    public void propertyChange(PropertyChangeEvent evt) {
        BuyAssetState state = buyAssetViewModel.getState();
        if (state.purchaseMessage != null) {
            JOptionPane.showMessageDialog(this, state.purchaseMessage);
            state.purchaseMessage = null;}
        if (state.errorMessage != null) {
            JOptionPane.showMessageDialog(this, state.errorMessage);
            state.errorMessage = null;}
        priceLabel.setText(state.price > 0 ? "Price: " + state.price : "Price: -");
        totalLabel.setText(state.total > 0 ? "Total: " + state.total : "Total: -");
    }

    public void setGetPriceController(GetPriceController controller) {
        this.getPriceController = controller;
    }

    public void setBuyAssetController(BuyAssetController controller) {
        this.buyAssetController = controller;
    }

    public void setLoggedInViewModel(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                refreshPortfolios();
            }
        });
        refreshPortfolios();
    }

    public void setSwitchLoggedInController(SwitchLoggedInController controller) {
        this.switchLoggedInController = controller;
    }

    public String getViewName() {
        return viewName;
    }
}
