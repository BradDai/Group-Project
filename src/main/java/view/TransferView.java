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

    // Stock-specific components
    private final JPanel stockPanel;
    private final JComboBox<String> stockSymbolDropdown;
    private final JSpinner stockAmountSpinner;
    private final JLabel stockValueLabel;

    // Currency-specific components
    private final JPanel currencyPanel;
    private final JComboBox<String> currencyTypeDropdown;
    private final JTextField currencyAmountField;
    private final JLabel currencyErrorLabel;

    // Buttons
    private final JButton confirmButton;
    private final JButton cancelButton;

    // Flag to prevent listener loops during updates
    private boolean isUpdating = false;

    public TransferView(TransferViewModel transferViewModel) {
        this.transferViewModel = transferViewModel;
        this.transferViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // From Portfolio Panel
        final JPanel fromPortfolioPanel = new JPanel();
        fromPortfolioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        fromPortfolioPanel.add(new JLabel("Transfer from Portfolio:"));
        fromPortfolioDropdown = new JComboBox<>();
        fromPortfolioPanel.add(fromPortfolioDropdown);
        this.add(fromPortfolioPanel);

        // To Portfolio Panel
        final JPanel toPortfolioPanel = new JPanel();
        toPortfolioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        toPortfolioPanel.add(new JLabel("Transfer to Portfolio:"));
        toPortfolioDropdown = new JComboBox<>();
        toPortfolioPanel.add(toPortfolioDropdown);
        this.add(toPortfolioPanel);

        // Transfer Type Panel
        final JPanel transferTypePanel = new JPanel();
        transferTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        transferTypePanel.add(new JLabel("Type of Transfer:"));
        transferTypeDropdown = new JComboBox<>(new String[]{"Stock", "Currency"});
        transferTypePanel.add(transferTypeDropdown);
        this.add(transferTypePanel);

        this.add(Box.createRigidArea(new Dimension(0, 10)));

        // Stock Panel (initially visible)
        stockPanel = new JPanel();
        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        stockPanel.setBorder(BorderFactory.createTitledBorder("Stock Transfer Details"));

        final JPanel stockSymbolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockSymbolPanel.add(new JLabel("Symbol of Stock:"));
        stockSymbolDropdown = new JComboBox<>();
        stockSymbolPanel.add(stockSymbolDropdown);
        stockPanel.add(stockSymbolPanel);

        final JPanel stockAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockAmountPanel.add(new JLabel("Amount of Stock:"));
        SpinnerNumberModel stockSpinnerModel = new SpinnerNumberModel(1, 1, 1000, 1);
        stockAmountSpinner = new JSpinner(stockSpinnerModel);
        stockAmountPanel.add(stockAmountSpinner);
        stockPanel.add(stockAmountPanel);

        final JPanel stockValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockValueLabel = new JLabel("Equivalent Value: $0.00");
        stockValueLabel.setFont(new Font("Arial", Font.BOLD, 12));
        stockValuePanel.add(stockValueLabel);
        stockPanel.add(stockValuePanel);

        this.add(stockPanel);

        // Currency Panel (initially hidden)
        currencyPanel = new JPanel();
        currencyPanel.setLayout(new BoxLayout(currencyPanel, BoxLayout.Y_AXIS));
        currencyPanel.setBorder(BorderFactory.createTitledBorder("Currency Transfer Details"));

        final JPanel currencyTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currencyTypePanel.add(new JLabel("Type of Currency:"));
        currencyTypeDropdown = new JComboBox<>();
        currencyTypePanel.add(currencyTypeDropdown);
        currencyPanel.add(currencyTypePanel);

        final JPanel currencyAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currencyAmountPanel.add(new JLabel("Amount of Currency:"));
        currencyAmountField = new JTextField(15);
        currencyAmountPanel.add(currencyAmountField);
        currencyPanel.add(currencyAmountPanel);

        currencyErrorLabel = new JLabel("");
        currencyErrorLabel.setForeground(Color.RED);
        currencyPanel.add(currencyErrorLabel);

        currencyPanel.setVisible(false);
        this.add(currencyPanel);

        this.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons Panel
        final JPanel buttonsPanel = new JPanel();
        confirmButton = new JButton("Confirm Transfer");
        cancelButton = new JButton("Cancel");

        buttonsPanel.add(confirmButton);
        buttonsPanel.add(cancelButton);
        this.add(buttonsPanel);

        // Add action listeners
        setupActionListeners();
    }

    private void setupActionListeners() {
        // Transfer type dropdown (toggle between stock and currency panels)
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
            revalidate();
            repaint();
        });

        // From portfolio dropdown, update available assets
        fromPortfolioDropdown.addActionListener(evt -> {
            if (isUpdating) return;
            String selectedType = (String) transferTypeDropdown.getSelectedItem();
            if ("Stock".equals(selectedType)) {
                updateStockDropdowns();
            } else {
                updateCurrencyDropdowns();
            }
        });

        // Stock symbol dropdown, update value calculation
        stockSymbolDropdown.addActionListener(evt -> {
            if (isUpdating) return;
            updateStockValue();
        });

        // Stock amount spinner, update value calculation
        stockAmountSpinner.addChangeListener(evt -> updateStockValue());

        // Currency amount field, validate available balance
        currencyAmountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateCurrencyAmount(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateCurrencyAmount(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateCurrencyAmount(); }
        });

        // Confirm button
        confirmButton.addActionListener(evt -> {
            if (validateTransfer()) {
                executeTransfer();
            }
        });

        // Cancel button
        cancelButton.addActionListener(evt -> {
            if (switchLoggedInController != null) {
                switchLoggedInController.switchToLoggedInView();
            }
        });

    }

    private void updateStockDropdowns() {
        String fromPortfolio = (String) fromPortfolioDropdown.getSelectedItem();
        if (fromPortfolio == null) {
            return;
        }

        TransferState currentState = transferViewModel.getState();
        currentState.setFromPortfolio(fromPortfolio);

        isUpdating = true;
        stockSymbolDropdown.removeAllItems();
        String[] availableStocks = currentState.getAvailableStocks();
        if (availableStocks != null) {
            for (String stock : availableStocks) {
                stockSymbolDropdown.addItem(stock);
            }
        }
        isUpdating = false;
    }

    private void updateCurrencyDropdowns() {
        String fromPortfolio = (String) fromPortfolioDropdown.getSelectedItem();
        if (fromPortfolio == null) {
            return;
        }

        TransferState currentState = transferViewModel.getState();
        currentState.setFromPortfolio(fromPortfolio);

        isUpdating = true;
        currencyTypeDropdown.removeAllItems();
        String[] availableCurrencies = currentState.getAvailableCurrencies();
        if (availableCurrencies != null) {
            for (String currency : availableCurrencies) {
                currencyTypeDropdown.addItem(currency);
            }
        }
        // Ensure USD is always there if empty (default fallback)
        if (currencyTypeDropdown.getItemCount() == 0) {
            currencyTypeDropdown.addItem("USD");
        }
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
                return;
            }

            double amount = Double.parseDouble(amountText);
            String currency = (String) currencyTypeDropdown.getSelectedItem();
            if (currency == null) currency = "USD"; // Fallback

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

    private boolean validateTransfer() {
        String fromPortfolio = (String) fromPortfolioDropdown.getSelectedItem();
        String toPortfolio = (String) toPortfolioDropdown.getSelectedItem();

        if (fromPortfolio == null || toPortfolio == null) {
            JOptionPane.showMessageDialog(this, "Please select both source and destination portfolios",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (fromPortfolio.equals(toPortfolio)) {
            JOptionPane.showMessageDialog(this, "Cannot transfer to the same portfolio",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void executeTransfer() {
        if (transferController == null) {
            JOptionPane.showMessageDialog(this, "Transfer controller not initialized",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = transferViewModel.getState().getUsername();
        String fromPortfolio = (String) fromPortfolioDropdown.getSelectedItem();
        String toPortfolio = (String) toPortfolioDropdown.getSelectedItem();
        String transferType = (String) transferTypeDropdown.getSelectedItem();

        if ("Stock".equals(transferType)) {
            String symbol = (String) stockSymbolDropdown.getSelectedItem();
            if (symbol == null) {
                JOptionPane.showMessageDialog(this, "Select a stock.");
                return;
            }
            int amount = (Integer) stockAmountSpinner.getValue();
            transferController.executeStockTransfer(username, fromPortfolio, toPortfolio, symbol, amount);
        } else {
            String currency = (String) currencyTypeDropdown.getSelectedItem();
            try {
                double amount = Double.parseDouble(currencyAmountField.getText().trim());
                transferController.executeCurrencyTransfer(username, fromPortfolio, toPortfolio, currency, amount);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid currency amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final TransferState state = (TransferState) evt.getNewValue();

            String[] portfolios = state.getAvailablePortfolios();
            isUpdating = true;

            fromPortfolioDropdown.removeAllItems();
            toPortfolioDropdown.removeAllItems();

            if (portfolios != null) {
                for (String portfolio : portfolios) {
                    fromPortfolioDropdown.addItem(portfolio);
                    toPortfolioDropdown.addItem(portfolio);
                }
            }

            isUpdating = false;

            this.revalidate();
            this.repaint();

        } else if (evt.getPropertyName().equals("error")) {
            TransferState state = (TransferState) evt.getNewValue();
            if (state.getError() != null && !state.getError().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getError(), "Transfer Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String getViewName() { return viewName; }
    public void setSwitchLoggedInController(SwitchLoggedInController c) { this.switchLoggedInController = c; }
    public void setTransferController(TransferController c) { this.transferController = c; }
}