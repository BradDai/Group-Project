package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONArray;
import org.json.JSONObject;

import interfaceadapter.SwitchLoggedInController;
import interfaceadapter.exchange.ExchangeController;
import interfaceadapter.exchange.ExchangeState;
import interfaceadapter.exchange.ExchangeViewModel;

public class ExchangeView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final String VIEW_NAME = "exchange";
    private final transient ExchangeViewModel exchangeViewModel;
    private transient ExchangeController exchangeController;
    private transient SwitchLoggedInController switchLoggedInController;

    private final JLabel resultLabel;
    private final JTextField amountField;

    private final JComboBox<String> selectedAccount;
    private final JComboBox<String> givenCurrency;
    private final JComboBox<String> gottenCurrency;

    private final JLabel balanceLabel;
    private final JLabel errorLabel;
    private final JLabel confirmationLabel;
    private final JLabel summaryLabel;

    private static final String ACCOUNT_DATA = "subaccounts.json";

    public ExchangeView(final ExchangeViewModel exchangeViewModel) {
        this.exchangeViewModel = exchangeViewModel;
        this.exchangeViewModel.addPropertyChangeListener(this);

        final JPanel buttons = new JPanel();
        final JButton back = new JButton("Cancel");
        final JButton confirmExchange = new JButton("Confirm Exchange");
        buttons.add(confirmExchange);
        buttons.add(back);

        final JLabel selectedAccountLabel = new JLabel("Select Account:");
        selectedAccount = new JComboBox<>();
        givenCurrency = new JComboBox<>();
        gottenCurrency = new JComboBox<>();

        final JLabel givenCurrencyLabel = new JLabel("Convert:");
        final JLabel gottenCurrencyLabel = new JLabel("To:");
        balanceLabel = new JLabel(" ");

        final JLabel amountLabel = new JLabel("Amount Of Currency To Be Converted:");
        amountField = new JTextField(15);

        amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updatePreview(); }
            @Override public void removeUpdate(DocumentEvent e) { updatePreview(); }
            @Override public void changedUpdate(DocumentEvent e) { updatePreview(); }
        });

        summaryLabel = new JLabel(" ");
        errorLabel = new JLabel(" ");
        confirmationLabel = new JLabel(" ");

        final JPanel balancePanel = new JPanel();
        balancePanel.add(balanceLabel);

        final JPanel ratePanel = new JPanel();
        ratePanel.add(new JLabel("Rate:"));
        resultLabel = new JLabel("N/A");
        ratePanel.add(resultLabel);

        final JPanel selectedAccountPanel = new JPanel();
        selectedAccountPanel.add(selectedAccountLabel);
        selectedAccountPanel.add(selectedAccount);

        final JPanel currencyInputPanel = new JPanel();
        currencyInputPanel.add(givenCurrencyLabel);
        currencyInputPanel.add(givenCurrency);
        currencyInputPanel.add(gottenCurrencyLabel);
        currencyInputPanel.add(gottenCurrency);

        final JPanel amountInputPanel = new JPanel();
        amountInputPanel.add(amountLabel);
        amountInputPanel.add(amountField);

        final JPanel summaryPanel = new JPanel();
        summaryPanel.add(summaryLabel);

        final JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(selectedAccountPanel);
        inputPanel.add(currencyInputPanel);
        inputPanel.add(ratePanel);
        inputPanel.add(balancePanel);
        inputPanel.add(amountInputPanel);

        final JPanel confirmPanel = new JPanel();
        confirmPanel.add(confirmationLabel);

        final JPanel errorPanel = new JPanel();
        errorPanel.add(errorLabel);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(inputPanel);
        this.add(summaryPanel);
        this.add(confirmPanel);
        this.add(errorPanel);
        this.add(buttons);

        back.addActionListener(evt -> switchLoggedInController.switchToLoggedInView());
        selectedAccount.addActionListener(actionEvent -> loadCurrenciesForSelectedAccount());

        givenCurrency.addActionListener(e -> {
            updateBalance();
            triggerRateQuery();
            updatePreview();
        });

        gottenCurrency.addActionListener(e -> {
            triggerRateQuery();
            updatePreview();
        });

        confirmExchange.addActionListener(actionEvent -> handleConfirmExchange());
    }

    private void loadAccounts() {

        selectedAccount.removeAllItems();
        final String username = exchangeViewModel.getExchangeState().getUsername();

        try {
            final String json = Files.readString(Paths.get(ACCOUNT_DATA), StandardCharsets.UTF_8);
            final JSONObject root = new JSONObject(json);

            if (root.has(username)) {
                final JSONArray accounts = root.getJSONArray(username);
                for (int i = 0; i < accounts.length(); i++) {
                    final JSONObject acc = accounts.getJSONObject(i);
                    selectedAccount.addItem(acc.getString("name"));
                }
            }

        }
        catch (final Exception exception) {
            System.err.println("Error loading accounts: " + exception.getMessage());
        }
    }

    private void loadCurrenciesForSelectedAccount() {

        givenCurrency.removeAllItems();
        gottenCurrency.removeAllItems();

        final String username = exchangeViewModel.getExchangeState().getUsername();
        final String accountName = (String) selectedAccount.getSelectedItem();

        if (accountName != null) {
            try {
                final String json = Files.readString(Paths.get(ACCOUNT_DATA), StandardCharsets.UTF_8);
                final JSONObject root = new JSONObject(json);

                final JSONArray accounts = root.getJSONArray(username);

                JSONObject accountObject = null;
                for (int i = 0; i < accounts.length(); i++) {
                    final JSONObject obj = accounts.getJSONObject(i);
                    if (accountName.equals(obj.getString("name"))) {
                        accountObject = obj;
                        break;
                    }
                }

                if (accountObject != null) {
                    final JSONObject ownedCurrencies = accountObject.getJSONObject("currencies");

                    for (final String key : ownedCurrencies.keySet()) {
                        givenCurrency.addItem(key);
                    }

                    final String currencyJson = Files.readString(Paths.get("currencies.json"));
                    final JSONArray allCurrencies = new JSONArray(currencyJson);
                    for (int i = 0; i < allCurrencies.length(); i++) {
                        gottenCurrency.addItem(allCurrencies.getString(i));
                    }
                }

            }
            catch (final Exception exception) {
                System.err.println("Error loading currencies: " + exception.getMessage());
            }
        }
    }

    private void triggerRateQuery() {

        final String from = (String) givenCurrency.getSelectedItem();
        final String to = (String) gottenCurrency.getSelectedItem();

        if (from != null && to != null) {
            exchangeController.getExchangeRate(from, to);
        }
    }

    private void handleConfirmExchange() {

        boolean finished = false;
        final String accountName = (String) selectedAccount.getSelectedItem();
        final String from = (String) givenCurrency.getSelectedItem();
        final String to = (String) gottenCurrency.getSelectedItem();
        final String amountText = amountField.getText();

        if (accountName == null || from == null || to == null) {
            errorLabel.setText("Please select account and currencies.");
        }
        else {
            double amount = 0;
            try {
                amount = Double.parseDouble(amountText);
            }
            catch (final NumberFormatException numberFormatException) {
                errorLabel.setText("Amount must be a valid number.");
                finished = true;
            }

            if (!finished) {
                exchangeController.convert(accountName, from, to, amount);
            }
        }
    }

    private void updateBalance() {

        balanceLabel.setText(" ");
        final String username = exchangeViewModel.getExchangeState().getUsername();
        final String accountName = (String) selectedAccount.getSelectedItem();
        final String currency = (String) givenCurrency.getSelectedItem();

        if (accountName == null || currency == null) {
            return;
        }

        try {
            final String json = Files.readString(Paths.get(ACCOUNT_DATA), StandardCharsets.UTF_8);
            final JSONObject root = new JSONObject(json);

            if (!root.has(username)) {
                return;
            }

            final JSONArray accounts = root.getJSONArray(username);

            for (int i = 0; i < accounts.length(); i++) {
                final JSONObject acc = accounts.getJSONObject(i);

                if (acc.getString("name").equals(accountName)) {
                    final JSONObject ownedCurrencies = acc.getJSONObject("currencies");

                    if (ownedCurrencies.has(currency)) {
                        final double balance = ownedCurrencies.getDouble(currency);
                        balanceLabel.setText(currency + " Balance: " + String.format("%.3f", balance));
                    }

                    return;
                }
            }

        }
        catch (final IOException e) {
            System.err.println("Error reading subaccounts.json: " + e.getMessage());
        }
    }

    private void updatePreview() {

        final String text = amountField.getText().trim();

        if (text.isEmpty()) {
            summaryLabel.setText(" ");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(text);
        }
        catch (NumberFormatException e) {
            summaryLabel.setText("Enter a valid number.");
            return;
        }

        double rate = exchangeViewModel.getRawRate();
        if (rate <= 0) {
            summaryLabel.setText("Waiting for valid exchange rate...");
            return;
        }

        final String from = (String) givenCurrency.getSelectedItem();
        final String to = (String) gottenCurrency.getSelectedItem();

        if (from == null || to == null) {
            summaryLabel.setText("Select currencies first.");
            return;
        }

        final double converted = amount * rate;

        summaryLabel.setText(String.format(
                "%.4f %s converts to %.8f %s",
                amount, from, converted, to
        ));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {

        if ("exchangeRate".equals(evt.getPropertyName())) {
            resultLabel.setText(evt.getNewValue().toString()); // ✔ show formatted rate
            updatePreview();                                  // ✔ uses rawRate
        }
        else if ("exchangeState".equals(evt.getPropertyName())) {
            final ExchangeState state = exchangeViewModel.getExchangeState();
            errorLabel.setText(state.getErrorMessage());
            confirmationLabel.setText(state.getConversionMessage());
            amountField.setText(state.getAmountField());
            loadAccounts();
        }
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public void setSwitchLoggedInController(final SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }

    public void setExchangeController(final ExchangeController exchangeController) {
        this.exchangeController = exchangeController;
        triggerRateQuery();
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        // Not used.
    }
}
