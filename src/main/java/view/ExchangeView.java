package view;

import interface_adapter.exchange.ExchangeController;
import interface_adapter.exchange.ExchangeState;
import interface_adapter.exchange.ExchangeViewModel;
import interface_adapter.SwitchLoggedInController;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExchangeView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final String VIEW_NAME = "exchange";
    private final transient ExchangeViewModel exchangeViewModel;
    private transient ExchangeController exchangeController;
    private transient SwitchLoggedInController switchLoggedInController;

    private final JButton back;
    private final JComboBox<String> firstCurrency;
    private final JComboBox<String> secondCurrency;
    private final JLabel resultLabel;
    private final JTextField amountField;
    private final JComboBox<String> selectedAccount;
    private final JComboBox<String> givenCurrency;
    private final JComboBox<String> gottenCurrency;
    private final JLabel givenCurrencyLabel;
    private final JLabel gottenCurrencyLabel;
    private final JLabel balanceLabel;
    private final JLabel amountLabel;
    private final JLabel selectedAccountLabel;
    private final JButton confirmExchange;
    private final JLabel errorLabel;
    private final JLabel confirmationLabel;
    private static final String ACCOUNT_DATA = "subaccounts.json";

    public ExchangeView(ExchangeViewModel exchangeViewModel) {
        this.exchangeViewModel = exchangeViewModel;
        this.exchangeViewModel.addPropertyChangeListener(this);

        final JPanel Buttons = new JPanel();

        back = new JButton("Cancel");
        confirmExchange = new JButton("Confirm");
        Buttons.add(confirmExchange);
        Buttons.add(back);

        firstCurrency = new JComboBox<>();
        secondCurrency = new JComboBox<>();
        loadGlobalCurrencies(firstCurrency, secondCurrency);

        selectedAccountLabel = new JLabel("Select Account:");
        selectedAccount = new JComboBox<>();
        givenCurrency = new JComboBox<>();
        gottenCurrency = new JComboBox<>();

        givenCurrencyLabel = new JLabel("Convert:");
        gottenCurrencyLabel = new JLabel("To:");
        balanceLabel = new JLabel(" ");
        amountLabel = new JLabel("Amount Of Currency To Be Converted:");
        amountField = new JTextField(15);

        errorLabel = new JLabel(" ");
        confirmationLabel = new JLabel(" ");

        JPanel currencyPanel = new JPanel();
        currencyPanel.add(new JLabel("From:"));
        currencyPanel.add(firstCurrency);
        currencyPanel.add(new JLabel("To:"));
        currencyPanel.add(secondCurrency);

        JPanel currencyBalancePanel = new JPanel();
        currencyBalancePanel.add(balanceLabel);

        JPanel resultPanel = new JPanel();
        resultPanel.add(new JLabel("Rate:"));
        resultLabel = new JLabel("N/A");
        resultPanel.add(resultLabel);

        JPanel selectedAccountPanel = new JPanel();
        selectedAccountPanel.add(selectedAccountLabel);
        selectedAccountPanel.add(selectedAccount);

        JPanel currencyInputPanel = new JPanel();
        currencyInputPanel.add(givenCurrencyLabel);
        currencyInputPanel.add(givenCurrency);
        currencyInputPanel.add(gottenCurrencyLabel);
        currencyInputPanel.add(gottenCurrency);

        JPanel amountInputPanel = new JPanel();
        amountInputPanel.add(amountLabel);
        amountInputPanel.add(amountField);

        JPanel inputPanel = new JPanel();
        inputPanel.add(selectedAccountPanel);
        inputPanel.add(currencyInputPanel);
        inputPanel.add(currencyBalancePanel);
        inputPanel.add(amountInputPanel);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JPanel confirmPanel = new JPanel();
        confirmPanel.add(confirmationLabel);

        JPanel errorPanel = new JPanel();
        errorPanel.add(errorLabel);

        this.add(currencyPanel);
        this.add(resultPanel);
        this.add(inputPanel);
        this.add(confirmPanel);
        this.add(errorPanel);
        this.add(Buttons);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        back.addActionListener(evt -> switchLoggedInController.switchToLoggedInView());

        selectedAccount.addActionListener(e -> loadCurrenciesForSelectedAccount());

        ActionListener updateSelection = evt -> triggerRateQuery();
        firstCurrency.addActionListener(updateSelection);
        secondCurrency.addActionListener(updateSelection);

        confirmExchange.addActionListener(e -> handleConfirmExchange());
        givenCurrency.addActionListener(e -> updateBalance());

    }

    private void loadGlobalCurrencies(JComboBox<String> first, JComboBox<String> second) {
        try {
            String json = Files.readString(Paths.get("currencies.json"), StandardCharsets.UTF_8);
            JSONArray arr = new JSONArray(json);

            first.removeAllItems();
            second.removeAllItems();

            for (int i = 0; i < arr.length(); i++) {
                first.addItem(arr.getString(i));
                second.addItem(arr.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAccounts() {

        selectedAccount.removeAllItems();
        String username = exchangeViewModel.getExchangeState().getUsername();

        try {
            String json = Files.readString(Paths.get(ACCOUNT_DATA), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(json);

            if (!root.has(username)) return;

            JSONArray accounts = root.getJSONArray(username);

            for (int i = 0; i < accounts.length(); i++) {
                JSONObject acc = accounts.getJSONObject(i);
                selectedAccount.addItem(acc.getString("name"));
            }

        } catch (Exception e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    private void loadCurrenciesForSelectedAccount() {
        givenCurrency.removeAllItems();
        gottenCurrency.removeAllItems();

        String username = exchangeViewModel.getExchangeState().getUsername();
        String accountName = (String) selectedAccount.getSelectedItem();
        if (accountName == null) return;

        try {
            String json = Files.readString(Paths.get(ACCOUNT_DATA), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(json);

            JSONArray accounts = root.getJSONArray(username);

            JSONObject accountObject = null;
            for (int i = 0; i < accounts.length(); i++) {
                JSONObject obj = accounts.getJSONObject(i);
                if (accountName.equals(obj.getString("name"))) {
                    accountObject = obj;
                    break;
                }
            }
            if (accountObject == null) return;

            JSONObject ownedCurrencies = accountObject.getJSONObject("currencies");

            for (String key : ownedCurrencies.keySet()) {
                givenCurrency.addItem(key);
            }

            String currencyJson = Files.readString(Paths.get("currencies.json"));
            JSONArray allCurrencies = new JSONArray(currencyJson);

            for (int i = 0; i < allCurrencies.length(); i++) {
                String code = allCurrencies.getString(i);
                gottenCurrency.addItem(code);
            }

        } catch (Exception e) {
            System.err.println("Error loading currencies: " + e.getMessage());
        }
    }

    private void triggerRateQuery() {
        String from = (String) firstCurrency.getSelectedItem();
        String to = (String) secondCurrency.getSelectedItem();

        if (from != null && to != null) {
            exchangeController.getExchangeRate(from, to);
        }
    }

    private void handleConfirmExchange() {

        String username = exchangeViewModel.getExchangeState().getUsername();
        String accountName = (String) selectedAccount.getSelectedItem();
        String from = (String) givenCurrency.getSelectedItem();
        String to = (String) gottenCurrency.getSelectedItem();
        String amountText = amountField.getText();

        if (accountName == null || from == null || to == null) {
            errorLabel.setText("Please select account and currencies.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Amount must be a valid number.");
            return;
        }

        if (exchangeController == null) {
            errorLabel.setText("Exchange controller not set.");
            return;
        }

        exchangeController.convert(username, accountName, from, to, amount);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("exchangeRate".equals(evt.getPropertyName())) {
            resultLabel.setText(evt.getNewValue().toString());
        }
        else if ("exchangeState".equals(evt.getPropertyName())) {
            ExchangeState state = exchangeViewModel.getExchangeState();
            errorLabel.setText(state.getErrorMessage());
            confirmationLabel.setText(state.getConversionMessage());
            amountField.setText(state.getAmountField());
            balanceLabel.setText(state.getBalanceMessage());
            loadAccounts();
        }
    }

    public String getViewName() { return VIEW_NAME; }

    public void setSwitchLoggedInController (SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }

    public void setExchangeController(ExchangeController exchangeController) {
        this.exchangeController = exchangeController;

        triggerRateQuery();
    }

    private void updateBalance() {
        balanceLabel.setText(" ");
        String username = exchangeViewModel.getExchangeState().getUsername();
        String accountName = (String) selectedAccount.getSelectedItem();
        String currency = (String) givenCurrency.getSelectedItem();

        if (accountName == null || currency == null) {
            return;
        }

        try {
            String json = Files.readString(Paths.get(ACCOUNT_DATA), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(json);

            if (!root.has(username)) {
                return;
            }

            JSONArray accounts = root.getJSONArray(username);

            for (int i = 0; i < accounts.length(); i++) {
                JSONObject acc = accounts.getJSONObject(i);

                if (acc.getString("name").equals(accountName)) {
                    JSONObject ownedCurrencies = acc.getJSONObject("currencies");

                    if (ownedCurrencies.has(currency)) {
                        double balance = ownedCurrencies.getDouble(currency);
                        balanceLabel.setText(currency + " Balance: " + String.format("%.3f", balance));
                    }

                    return;
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading subaccounts.json: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        // This method is not needed to be used.
    }
}
