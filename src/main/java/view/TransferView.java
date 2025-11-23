package view;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.transfer.TransferController;
import interface_adapter.transfer.TransferState;
import interface_adapter.transfer.TransferViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TransferView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "transfer";
    private final TransferViewModel transferViewModel;
    private SwitchLoggedInController switchLoggedInController;
    private TransferController transferController;

    // UI Components
    private final JComboBox<String> fromPortfolioDropdown;
    private final JComboBox<String> toPortfolioDropdown;
    private final JComboBox<String> transferTypeDropdown;

    // Stock components
    private final JPanel stockPanel;
    private final JComboBox<String> stockSymbolDropdown;
    private final JSpinner stockAmountSpinner;
    private final JLabel stockValueLabel;

    // Currency components
    private final JPanel currencyPanel;
    private final JComboBox<String> currencyTypeDropdown;
    private final JTextField currencyAmountField;
    private final JLabel currencyErrorLabel;

    // Labels for Balances
    private final JLabel fromBalanceLabel;
    private final JLabel toBalanceLabel;

    // Buttons
    private final JButton confirmButton;
    private final JButton cancelButton;

    private boolean isUpdating = false;

    public TransferView(TransferViewModel transferViewModel) {
        this.transferViewModel = transferViewModel;
        this.transferViewModel.addPropertyChangeListener(this);

        this.setName(viewName);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Subaccount selection
        final JPanel fromPortfolioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fromPortfolioPanel.add(new JLabel("Transfer from Portfolio:"));
        fromPortfolioDropdown = new JComboBox<>();
        fromPortfolioPanel.add(fromPortfolioDropdown);
        this.add(fromPortfolioPanel);

        final JPanel toPortfolioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toPortfolioPanel.add(new JLabel("Transfer to Portfolio:"));
        toPortfolioDropdown = new JComboBox<>();
        toPortfolioPanel.add(toPortfolioDropdown);
        this.add(toPortfolioPanel);

        final JPanel transferTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transferTypePanel.add(new JLabel("Type of Transfer:"));
        transferTypeDropdown = new JComboBox<>(new String[]{"Stock", "Currency"});
        transferTypePanel.add(transferTypeDropdown);
        this.add(transferTypePanel);

        this.add(Box.createRigidArea(new Dimension(0, 10)));

        // Stock panel
        stockPanel = new JPanel();
        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        stockPanel.setBorder(BorderFactory.createTitledBorder("Stock Transfer Details"));

        JPanel stockSymbolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockSymbolPanel.add(new JLabel("Symbol:"));
        stockSymbolDropdown = new JComboBox<>();
        stockSymbolPanel.add(stockSymbolDropdown);
        stockPanel.add(stockSymbolPanel);

        JPanel stockAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockAmountPanel.add(new JLabel("Amount:"));
        stockAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        stockAmountPanel.add(stockAmountSpinner);
        stockPanel.add(stockAmountPanel);

        JPanel stockValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockValueLabel = new JLabel("Equivalent Value: $0.00");
        stockValuePanel.add(stockValueLabel);
        stockPanel.add(stockValuePanel);
        this.add(stockPanel);

        // Currency panel
        currencyPanel = new JPanel();
        currencyPanel.setLayout(new BoxLayout(currencyPanel, BoxLayout.Y_AXIS));
        currencyPanel.setBorder(BorderFactory.createTitledBorder("Currency Transfer Details"));

        // Currency Selection
        JPanel currencyTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currencyTypePanel.add(new JLabel("Currency:"));
        currencyTypeDropdown = new JComboBox<>();
        currencyTypePanel.add(currencyTypeDropdown);
        currencyPanel.add(currencyTypePanel);

        // Balance Info
        JPanel balanceInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Box balanceBox = Box.createVerticalBox();

        fromBalanceLabel = new JLabel("Sender Balance: -");
        toBalanceLabel = new JLabel("Receiver Balance: -");
        fromBalanceLabel.setForeground(Color.DARK_GRAY);
        toBalanceLabel.setForeground(Color.DARK_GRAY);

        balanceBox.add(fromBalanceLabel);
        balanceBox.add(Box.createVerticalStrut(3));
        balanceBox.add(toBalanceLabel);

        balanceInfoPanel.add(balanceBox);
        currencyPanel.add(balanceInfoPanel);

        // Amount Input
        JPanel currencyAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currencyAmountPanel.add(new JLabel("Amount:"));
        currencyAmountField = new JTextField(15);
        currencyAmountPanel.add(currencyAmountField);
        currencyPanel.add(currencyAmountPanel);

        // Error Label
        currencyErrorLabel = new JLabel("");
        currencyErrorLabel.setForeground(Color.RED);
        currencyErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currencyPanel.add(currencyErrorLabel);

        currencyPanel.setVisible(false);
        this.add(currencyPanel);

        this.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        final JPanel buttonsPanel = new JPanel();
        confirmButton = new JButton("Confirm Transfer");
        cancelButton = new JButton("Cancel");
        buttonsPanel.add(confirmButton);
        buttonsPanel.add(cancelButton);
        this.add(buttonsPanel);

        setupActionListeners();
    }

    private void setupActionListeners() {
        ActionListener updateBalancesListener = e -> {
            if (!isUpdating) triggerBalanceCheck();
        };

        fromPortfolioDropdown.addActionListener(e -> {
            if (isUpdating) return;
            String selectedType = (String) transferTypeDropdown.getSelectedItem();
            if ("Stock".equals(selectedType)) {
                updateStockDropdowns();
            } else {
                updateCurrencyDropdowns();
            }
            triggerBalanceCheck();
        });

        toPortfolioDropdown.addActionListener(updateBalancesListener);

        transferTypeDropdown.addActionListener(evt -> {
            if (isUpdating) return;
            String selectedType = (String) transferTypeDropdown.getSelectedItem();
            if ("Stock".equals(selectedType)) {
                stockPanel.setVisible(true);
                currencyPanel.setVisible(false);
                updateStockDropdowns();
            } else if ("Currency".equals(selectedType)) {
                stockPanel.setVisible(false);
                currencyPanel.setVisible(true);
                updateCurrencyDropdowns();
            }
            triggerBalanceCheck();
            revalidate();
            repaint();

            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.pack();
        });

        stockSymbolDropdown.addActionListener(e -> {
            if (isUpdating) return;
            updateStockValue();
        });

        currencyTypeDropdown.addActionListener(e -> {
            if (!isUpdating) triggerBalanceCheck();
        });

        stockAmountSpinner.addChangeListener(evt -> updateStockValue());

        currencyAmountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateCurrencyAmount(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateCurrencyAmount(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateCurrencyAmount(); }
        });

        confirmButton.addActionListener(evt -> executeTransfer());
        cancelButton.addActionListener(evt -> {
            if (switchLoggedInController != null) switchLoggedInController.switchToLoggedInView();
        });
    }

    private void triggerBalanceCheck() {
        if (transferController == null) return;

        String username = transferViewModel.getState().getUsername();
        String from = (String) fromPortfolioDropdown.getSelectedItem();
        String to = (String) toPortfolioDropdown.getSelectedItem();
        String type = (String) transferTypeDropdown.getSelectedItem();
        String asset = "";

        if (from == null || to == null) return;

        if ("Currency".equals(type)) {
            asset = (String) currencyTypeDropdown.getSelectedItem();
            if (asset == null) asset = "USD";
        } else {
            asset = "USD";
        }

        transferController.checkBalances(username, from, to, asset);
    }

    private void updateStockDropdowns() {
        String from = (String) fromPortfolioDropdown.getSelectedItem();
        if (from == null) return;
        TransferState state = transferViewModel.getState();
        state.setFromPortfolio(from);
        isUpdating = true;
        stockSymbolDropdown.removeAllItems();
        if (state.getAvailableStocks() != null) {
            for (String s : state.getAvailableStocks()) stockSymbolDropdown.addItem(s);
        }
        isUpdating = false;
    }

    private void updateCurrencyDropdowns() {
        String from = (String) fromPortfolioDropdown.getSelectedItem();
        if (from == null) return;
        TransferState state = transferViewModel.getState();
        state.setFromPortfolio(from);
        isUpdating = true;
        currencyTypeDropdown.removeAllItems();
        if (state.getAvailableCurrencies() != null) {
            for (String c : state.getAvailableCurrencies()) currencyTypeDropdown.addItem(c);
        }
        if (currencyTypeDropdown.getItemCount() == 0) currencyTypeDropdown.addItem("USD");
        isUpdating = false;
    }

    private void updateStockValue() {
        String symbol = (String) stockSymbolDropdown.getSelectedItem();
        if (symbol == null) {
            stockValueLabel.setText("Equivalent Value: $0.00");
            return;
        }
        int amount = (Integer) stockAmountSpinner.getValue();
        TransferState currentState = transferViewModel.getState();
        double pricePerShare = currentState.getStockPrice(symbol);
        double totalValue = pricePerShare * amount;
        stockValueLabel.setText(String.format("Equivalent Value: $%.2f", totalValue));
    }

    private void validateCurrencyAmount() {
        try {
            String amountText = currencyAmountField.getText().trim();
            if (amountText.isEmpty()) {
                currencyErrorLabel.setText("");
                confirmButton.setEnabled(true);
                return;
            }
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                currencyErrorLabel.setText("Amount must be positive");
                confirmButton.setEnabled(false);
                return;
            }
            currencyErrorLabel.setText("");
            confirmButton.setEnabled(true);
        } catch (NumberFormatException e) {
            currencyErrorLabel.setText("Invalid amount format");
            currencyErrorLabel.setForeground(Color.RED);
            confirmButton.setEnabled(false);
        }
    }

    private void executeTransfer() {
        if (transferController == null) {
            JOptionPane.showMessageDialog(this, "Transfer controller not initialized", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String username = transferViewModel.getState().getUsername();
        String from = (String) fromPortfolioDropdown.getSelectedItem();
        String to = (String) toPortfolioDropdown.getSelectedItem();
        String type = (String) transferTypeDropdown.getSelectedItem();

        if ("Stock".equals(type)) {
            String symbol = (String) stockSymbolDropdown.getSelectedItem();
            if (symbol == null) { JOptionPane.showMessageDialog(this, "Select a stock."); return; }
            int amount = (Integer) stockAmountSpinner.getValue();
            transferController.executeStockTransfer(username, from, to, symbol, amount);
        } else {
            String currency = (String) currencyTypeDropdown.getSelectedItem();
            try {
                double amount = Double.parseDouble(currencyAmountField.getText().trim());
                transferController.executeCurrencyTransfer(username, from, to, currency, amount);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid currency amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Click " + e.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            TransferState state = (TransferState) evt.getNewValue();

            isUpdating = true;

            String[] newPortfolios = state.getAvailablePortfolios();
            boolean listsDiffer = false;

            // Check if the portfolio list in State differs from the Dropdown items
            if (newPortfolios == null) {
                if (fromPortfolioDropdown.getItemCount() > 0) listsDiffer = true;
            } else if (fromPortfolioDropdown.getItemCount() != newPortfolios.length) {
                listsDiffer = true;
            } else {
                for (int i = 0; i < newPortfolios.length; i++) {
                    if (!newPortfolios[i].equals(fromPortfolioDropdown.getItemAt(i))) {
                        listsDiffer = true;
                        break;
                    }
                }
            }

            // If list changed (like item deleted), repopulate dropdowns
            if (listsDiffer && newPortfolios != null) {
                fromPortfolioDropdown.removeAllItems();
                toPortfolioDropdown.removeAllItems();

                for (String p : newPortfolios) {
                    fromPortfolioDropdown.addItem(p);
                    toPortfolioDropdown.addItem(p);
                }

                // Set defaults
                if (fromPortfolioDropdown.getItemCount() > 0) fromPortfolioDropdown.setSelectedIndex(0);
                if (toPortfolioDropdown.getItemCount() > 1) toPortfolioDropdown.setSelectedIndex(1);

                // Trigger update since we changed selection
                SwingUtilities.invokeLater(() -> triggerBalanceCheck());
                this.revalidate();
            }

            // Update labels
            fromBalanceLabel.setText("Sender Balance: " + state.getFromBalance());
            toBalanceLabel.setText("Receiver Balance: " + state.getToBalance());

            // Update amount if changed
            if (!currencyAmountField.getText().equals(state.getAmount())) {
                currencyAmountField.setText(state.getAmount());
            }

            isUpdating = false;
            this.repaint();

        } else if ("error".equals(evt.getPropertyName())) {
            TransferState state = (TransferState) evt.getNewValue();
            if (state.getError() != null && !state.getError().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String getViewName() { return viewName; }
    public void setSwitchLoggedInController(SwitchLoggedInController c) { this.switchLoggedInController = c; }
    public void setTransferController(TransferController c) { this.transferController = c; }
}