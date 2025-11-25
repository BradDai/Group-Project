package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.transfer.TransferController;
import interface_adapter.transfer.TransferState;
import interface_adapter.transfer.TransferViewModel;

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

    // Currency components
    private final JPanel currencyPanel;
    private final JComboBox<String> currencyTypeDropdown;
    private final JTextField currencyAmountField;
    private final JLabel currencyErrorLabel;

    // Labels for Balances
    private final JPanel balanceInfoPanel;
    private final JLabel fromBalanceLabel;
    private final JLabel toBalanceLabel;

    // Buttons
    private final JButton confirmButton;
    private final JButton cancelButton;

    private boolean isUpdating;

    public TransferView(final TransferViewModel transferViewModel) {
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
        transferTypeDropdown = new JComboBox<>(new String[] {"Stock", "Currency"});
        transferTypePanel.add(transferTypeDropdown);
        this.add(transferTypePanel);

        balanceInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final Box balanceBox = Box.createVerticalBox();

        fromBalanceLabel = new JLabel("Sender Balance: -");
        toBalanceLabel = new JLabel("Receiver Balance: -");
        fromBalanceLabel.setForeground(Color.DARK_GRAY);
        toBalanceLabel.setForeground(Color.DARK_GRAY);

        balanceBox.add(fromBalanceLabel);
        balanceBox.add(Box.createVerticalStrut(3));
        balanceBox.add(toBalanceLabel);

        balanceInfoPanel.add(balanceBox);

        this.add(Box.createRigidArea(new Dimension(0, 10)));

        // Stock panel
        stockPanel = new JPanel();
        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        stockPanel.setBorder(BorderFactory.createTitledBorder("Stock Transfer Details"));

        final JPanel stockSymbolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockSymbolPanel.add(new JLabel("Symbol:"));
        stockSymbolDropdown = new JComboBox<>();
        stockSymbolPanel.add(stockSymbolDropdown);
        stockPanel.add(stockSymbolPanel);

        // Add balance info here initially
        stockPanel.add(balanceInfoPanel);

        final JPanel stockAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockAmountPanel.add(new JLabel("Quantity:"));
        stockAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        stockAmountPanel.add(stockAmountSpinner);
        stockPanel.add(stockAmountPanel);

        this.add(stockPanel);

        // Currency panel
        currencyPanel = new JPanel();
        currencyPanel.setLayout(new BoxLayout(currencyPanel, BoxLayout.Y_AXIS));
        currencyPanel.setBorder(BorderFactory.createTitledBorder("Currency Transfer Details"));

        // Currency Selection
        final JPanel currencyTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currencyTypePanel.add(new JLabel("Currency:"));
        currencyTypeDropdown = new JComboBox<>();
        currencyTypePanel.add(currencyTypeDropdown);
        currencyPanel.add(currencyTypePanel);

        // Amount Input
        final JPanel currencyAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
        isUpdating = false;
        isUpdating = false;
    }

    private void setupActionListeners() {
        final ActionListener updateBalancesListener = actionEvent -> {
            if (!isUpdating) {
                triggerBalanceCheck();
            }
        };

        fromPortfolioDropdown.addActionListener(actionEvent -> {
            if (!isUpdating) {
                final String selectedType = (String) transferTypeDropdown.getSelectedItem();
                if ("Stock".equals(selectedType)) {
                    updateStockDropdowns();
                }
                else {
                    updateCurrencyDropdowns();
                }
                triggerBalanceCheck();
            }
        });

        toPortfolioDropdown.addActionListener(updateBalancesListener);

        transferTypeDropdown.addActionListener(actionEvent -> {
            if (!isUpdating) {
                final String selectedType = (String) transferTypeDropdown.getSelectedItem();
                if ("Stock".equals(selectedType)) {
                    stockPanel.setVisible(true);
                    currencyPanel.setVisible(false);
                    updateStockDropdowns();
                    updateLabelPrefix("Quantity");
                    stockPanel.add(balanceInfoPanel, 1);
                }
                else if ("Currency".equals(selectedType)) {
                    stockPanel.setVisible(false);
                    currencyPanel.setVisible(true);
                    updateCurrencyDropdowns();
                    updateLabelPrefix("Balance");
                    currencyPanel.add(balanceInfoPanel, 1);
                }
                triggerBalanceCheck();
                revalidate();
                repaint();

                final Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.pack();
                }
            }
        });

        stockSymbolDropdown.addActionListener(actionEvent -> {
            if (!isUpdating) {
                triggerBalanceCheck();
            }
        });

        currencyTypeDropdown.addActionListener(actionEvent -> {
            if (!isUpdating) {
                triggerBalanceCheck();
            }
        });

        currencyAmountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(final javax.swing.event.DocumentEvent documentEvent) {
                validateCurrencyAmount();
            }

            public void removeUpdate(final javax.swing.event.DocumentEvent documentEvent) {
                validateCurrencyAmount();
            }

            public void changedUpdate(final javax.swing.event.DocumentEvent documentEvent) {
                validateCurrencyAmount();
            }
        });

        confirmButton.addActionListener(evt -> executeTransfer());
        cancelButton.addActionListener(evt -> {
            if (switchLoggedInController != null) {
                switchLoggedInController.switchToLoggedInView();
            }
        });
    }

    private void updateLabelPrefix(String type) {
    }

    private void triggerBalanceCheck() {
        if (transferController != null) {
            final String username = transferViewModel.getState().getUsername();
            final String from = (String) fromPortfolioDropdown.getSelectedItem();
            final String to = (String) toPortfolioDropdown.getSelectedItem();
            final String type = (String) transferTypeDropdown.getSelectedItem();
            String asset = "USD";
            if (from != null && to != null) {
                if ("Currency".equals(type)) {
                    asset = (String) currencyTypeDropdown.getSelectedItem();
                    if (asset == null) {
                        asset = "USD";
                    }
                }
                else {
                    asset = (String) stockSymbolDropdown.getSelectedItem();
                    if (asset == null) {
                        asset = "USD";
                    }
                }
                transferController.checkBalances(username, from, to, asset);
            }
        }

    }

    private void updateStockDropdowns() {
        final String from = (String) fromPortfolioDropdown.getSelectedItem();
        if (from != null) {
            final TransferState state = transferViewModel.getState();
            state.setFromPortfolio(from);
            isUpdating = true;
            stockSymbolDropdown.removeAllItems();
            if (state.getAvailableStocks() != null) {
                for (final String s : state.getAvailableStocks()) {
                    stockSymbolDropdown.addItem(s);
                }
            }
            isUpdating = false;
        }
    }

    private void updateCurrencyDropdowns() {
        final String from = (String) fromPortfolioDropdown.getSelectedItem();
        if (from != null) {
            final TransferState state = transferViewModel.getState();
            state.setFromPortfolio(from);
            isUpdating = true;
            final Object currentSelection = currencyTypeDropdown.getSelectedItem();
            currencyTypeDropdown.removeAllItems();
            if (state.getAvailableCurrencies() != null) {
                for (final String c : state.getAvailableCurrencies()) {
                    currencyTypeDropdown.addItem(c);
                }
            }
            if (currencyTypeDropdown.getItemCount() == 0) {
                currencyTypeDropdown.addItem("USD");
            }
            if (currentSelection != null) {
                for (int i = 0; i < currencyTypeDropdown.getItemCount(); i++) {
                    if (currencyTypeDropdown.getItemAt(i).equals(currentSelection)) {
                        currencyTypeDropdown.setSelectedIndex(i);
                        break;
                    }
                }
            }
            isUpdating = false;
        }

    }

    private void validateCurrencyAmount() {
        try {
            final String amountText = currencyAmountField.getText().trim();
            if (amountText.isEmpty()) {
                currencyErrorLabel.setText("");
                confirmButton.setEnabled(true);
            }
            else {
                final double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    currencyErrorLabel.setText("Amount must be positive");
                    confirmButton.setEnabled(false);
                }
                else {
                    currencyErrorLabel.setText("");
                    confirmButton.setEnabled(true);
                }
            }
        }
        catch (final NumberFormatException numberFormatException) {
            currencyErrorLabel.setText("Invalid amount format");
            currencyErrorLabel.setForeground(Color.RED);
            confirmButton.setEnabled(false);
        }
    }

    private void executeTransfer() {
        if (transferController == null) {
            JOptionPane.showMessageDialog(this, "Transfer controller not initialized", "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        else {
            final String username = transferViewModel.getState().getUsername();
            final String from = (String) fromPortfolioDropdown.getSelectedItem();
            final String to = (String) toPortfolioDropdown.getSelectedItem();
            final String type = (String) transferTypeDropdown.getSelectedItem();
            if ("Stock".equals(type)) {
                final String symbol = (String) stockSymbolDropdown.getSelectedItem();
                if (symbol == null) {
                    JOptionPane.showMessageDialog(this, "Select a stock.");
                }
                else {
                    final int amount = (Integer) stockAmountSpinner.getValue();
                    transferController.executeStockTransfer(username, from, to, symbol, amount);
                }
            }
            else {
                final String currency = (String) currencyTypeDropdown.getSelectedItem();
                try {
                    final double amount = Double.parseDouble(currencyAmountField.getText().trim());
                    transferController.executeCurrencyTransfer(username, from, to, currency, amount);
                }
                catch (final NumberFormatException numberFormatException) {
                    JOptionPane.showMessageDialog(this, "Invalid currency amount", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        System.out.println("Click " + e.getActionCommand());
    }

    public String getViewName() {
        return viewName;
    }

    public void setSwitchLoggedInController(final SwitchLoggedInController loggedInController) {
        this.switchLoggedInController = loggedInController;
    }

    public void setTransferController(final TransferController transferController) {
        this.transferController = transferController;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final TransferState state = (TransferState) evt.getNewValue();

            isUpdating = true;

            final String[] newPortfolios = state.getAvailablePortfolios();
            boolean listsDiffer = false;

            if (newPortfolios == null) {
                if (fromPortfolioDropdown.getItemCount() > 0) {
                    listsDiffer = true;
                }
            }
            else if (fromPortfolioDropdown.getItemCount() != newPortfolios.length) {
                listsDiffer = true;
            }
            else {
                for (int i = 0; i < newPortfolios.length; i++) {
                    if (!newPortfolios[i].equals(fromPortfolioDropdown.getItemAt(i))) {
                        listsDiffer = true;
                        break;
                    }
                }
            }

            if (listsDiffer && newPortfolios != null) {
                fromPortfolioDropdown.removeAllItems();
                toPortfolioDropdown.removeAllItems();
                for (final String p : newPortfolios) {
                    fromPortfolioDropdown.addItem(p);
                    toPortfolioDropdown.addItem(p);
                }
                if (fromPortfolioDropdown.getItemCount() > 0) {
                    fromPortfolioDropdown.setSelectedIndex(0);
                }
                if (toPortfolioDropdown.getItemCount() > 1) {
                    toPortfolioDropdown.setSelectedIndex(1);
                }
                SwingUtilities.invokeLater(this::triggerBalanceCheck);
                this.revalidate();
            }

            final String[] newCurrencies = state.getAvailableCurrencies();
            boolean currenciesDiffer = false;
            if (newCurrencies == null) {
                if (currencyTypeDropdown.getItemCount() > 0) {
                    currenciesDiffer = true;
                }
            }
            else if (currencyTypeDropdown.getItemCount() != newCurrencies.length) {
                currenciesDiffer = true;
            }
            else {
                for (int i = 0; i < newCurrencies.length; i++) {
                    boolean found = false;
                    for (int j = 0; j < currencyTypeDropdown.getItemCount(); j++) {
                        if (currencyTypeDropdown.getItemAt(j).equals(newCurrencies[i])) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        currenciesDiffer = true;
                        break;
                    }
                }
            }

            if (currenciesDiffer && newCurrencies != null) {
                final Object sel = currencyTypeDropdown.getSelectedItem();
                currencyTypeDropdown.removeAllItems();
                for (final String c : newCurrencies) {
                    currencyTypeDropdown.addItem(c);
                }
                if (sel != null) {
                    currencyTypeDropdown.setSelectedItem(sel);
                }
            }

            final String[] newStocks = state.getAvailableStocks();
            boolean stocksDiffer = false;
            if (newStocks == null) {
                if (stockSymbolDropdown.getItemCount() > 0) {
                    stocksDiffer = true;
                }
            }
            else if (stockSymbolDropdown.getItemCount() != newStocks.length) {
                stocksDiffer = true;
            }
            else {
                for (int i = 0; i < newStocks.length; i++) {
                    boolean found = false;
                    for (int j = 0; j < stockSymbolDropdown.getItemCount(); j++) {
                        if (stockSymbolDropdown.getItemAt(j).equals(newStocks[i])) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        stocksDiffer = true;
                        break;
                    }
                }
            }

            if (stocksDiffer && newStocks != null) {
                final Object sel = stockSymbolDropdown.getSelectedItem();
                stockSymbolDropdown.removeAllItems();
                for (final String s : newStocks) {
                    stockSymbolDropdown.addItem(s);
                }
                if (sel != null) {
                    stockSymbolDropdown.setSelectedItem(sel);
                }
                else if (stockSymbolDropdown.getItemCount() > 0) {
                    stockSymbolDropdown.setSelectedIndex(0);
                }

                SwingUtilities.invokeLater(this::triggerBalanceCheck);
            }

            String typeLabel = "Balance";
            if ("Stock".equals(transferTypeDropdown.getSelectedItem())) {
                typeLabel = "Quantity";
            }

            fromBalanceLabel.setText("Sender " + typeLabel + ": " + state.getFromBalance());
            toBalanceLabel.setText("Receiver " + typeLabel + ": " + state.getToBalance());

            if (!currencyAmountField.getText().equals(state.getAmount())) {
                currencyAmountField.setText(state.getAmount());
            }

            isUpdating = false;
            this.repaint();

        }
        else if ("error".equals(evt.getPropertyName())) {
            final TransferState state = (TransferState) evt.getNewValue();
            if (state.getError() != null && !state.getError().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
