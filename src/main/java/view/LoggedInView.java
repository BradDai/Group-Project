package view;

import entity.Asset;
import entity.SubAccount;
import interface_adapter.exchange.ExchangeController;
import interface_adapter.logged_in.*;
import interface_adapter.logout.LogoutController;
import interface_adapter.subaccount.create.CreateSubAccountController;
import interface_adapter.subaccount.delete.DeleteSubAccountController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    public static final String VIEW_NAME = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private ChangePasswordController changePasswordController;
    private CreateSubAccountController createSubAccountController;
    private LogoutController logoutController;
    private ExchangeController exchangeController;
    private SwitchExchangeController switchExchangeController;
    private SwitchTransferController switchTransferController;
    private SwitchHistoryController switchHistoryController;
    private SwitchBuyAssetController switchBuyAssetController;
    private SwitchSellAssetController switchSellAssetController;
    private DeleteSubAccountController deleteSubAccountController;

    private final JLabel userLabel = new JLabel("User");
    private final JButton logoutButton = new JButton("Log out");
    private final JButton changePasswordButton = new JButton("Change Password");
    private final JButton createSubButton = new JButton("Create Subaccount");
    private final JButton deleteSubButton = new JButton("Delete Subaccount");
    private final JButton buyAssetButton = new JButton("Buy Asset");
    private final JButton sellAssetButton = new JButton("Sell Asset");
    private final JButton convertCurrencyButton = new JButton("Convert Currency");
    private final JButton transferMoneyButton = new JButton("Transfer Money");
    private final JButton historyButton = new JButton("Show Transaction History");

    private static final int MAX_SUBACCOUNTS = 5;
    private final JPanel[] subAccountPanels = new JPanel[MAX_SUBACCOUNTS];
    private final JLabel[] subAccountNameLabels = new JLabel[MAX_SUBACCOUNTS];
    private final JLabel[] subAccountCurrencyLabels = new JLabel[MAX_SUBACCOUNTS];
    private final JLabel[] subAccountStockLabels = new JLabel[MAX_SUBACCOUNTS];

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftTop.add(userLabel);
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        rightTop.add(logoutButton);
        rightTop.add(changePasswordButton);
        rightTop.add(createSubButton);
        rightTop.add(deleteSubButton);
        topPanel.add(leftTop, BorderLayout.WEST);
        topPanel.add(rightTop, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel accountsRow = new JPanel(new GridLayout(1, MAX_SUBACCOUNTS, 10, 0));
        for (int i = 0; i < MAX_SUBACCOUNTS; i++) {
            JPanel slot = new JPanel();
            slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));
            slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            JLabel nameLabel = new JLabel();
            JLabel currencyLabel = new JLabel();
            JLabel stockLabel = new JLabel();
            if (i == 0) {
                nameLabel.setText("Main USD Portfolio");
                currencyLabel.setText("Currency  USD: 1,000,000.00");
                stockLabel.setText("Stock: (none)");
            } else {
                nameLabel.setText("Empty slot");
                currencyLabel.setText("Currency  USD: -");
                stockLabel.setText("Stock: (none)");
            }
            subAccountNameLabels[i] = nameLabel;
            subAccountCurrencyLabels[i] = currencyLabel;
            subAccountStockLabels[i] = stockLabel;
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            currencyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            slot.add(Box.createVerticalStrut(5));
            slot.add(nameLabel);
            slot.add(Box.createVerticalStrut(10));
            slot.add(currencyLabel);
            slot.add(Box.createVerticalStrut(5));
            slot.add(stockLabel);
            slot.add(Box.createVerticalGlue());
            accountsRow.add(slot);
            subAccountPanels[i] = slot;
        }
        centerPanel.add(accountsRow, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        bottomPanel.add(buyAssetButton);
        bottomPanel.add(sellAssetButton);
        bottomPanel.add(convertCurrencyButton);
        bottomPanel.add(transferMoneyButton);
        bottomPanel.add(historyButton);
        bottomPanel.add(new JLabel());
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        logoutButton.addActionListener(e -> { if (logoutController != null) logoutController.execute(); });

        changePasswordButton.addActionListener(e -> {
            if (changePasswordController == null) return;
            String username = loggedInViewModel.getState().getUsername();
            String newPassword = JOptionPane.showInputDialog(this, "Enter new password:", "Change Password", JOptionPane.PLAIN_MESSAGE);
            if (newPassword != null && !newPassword.isEmpty()) changePasswordController.execute(username, newPassword);
        });

        createSubButton.addActionListener(e -> {
            if (createSubAccountController == null) return;
            String name = JOptionPane.showInputDialog(this, "Enter new subaccount name:", "Create Subaccount", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) createSubAccountController.execute(loggedInViewModel.getState().getUsername(), name.trim());
        });

        deleteSubButton.addActionListener(e -> {
            if (deleteSubAccountController == null) return;
            String name = JOptionPane.showInputDialog(this, "Enter subaccount name to delete:", "Delete Subaccount", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) deleteSubAccountController.execute(loggedInViewModel.getState().getUsername(), name.trim());
        });

        convertCurrencyButton.addActionListener(e -> { if (switchExchangeController != null) switchExchangeController.switchToExchangeView(); });

        transferMoneyButton.addActionListener(e -> {
            if (switchTransferController != null) {
                String username = loggedInViewModel.getState().getUsername();
                switchTransferController.switchToTransferView(username);
            }
        });

        historyButton.addActionListener(e -> { if (switchHistoryController != null) switchHistoryController.switchToHistoryView(); });
        buyAssetButton.addActionListener(e -> { if (switchBuyAssetController != null) switchBuyAssetController.switchToBuyAssetView(); });
        sellAssetButton.addActionListener(e -> { if (switchSellAssetController != null) switchSellAssetController.switchToSellAssetView(); });
    }

    // Setters
    public void setLogoutController(LogoutController c) { this.logoutController = c; }
    public void setChangePasswordController(ChangePasswordController c) { this.changePasswordController = c; }
    public void setCreateSubAccountController(CreateSubAccountController c) { this.createSubAccountController = c; }
    public void setDeleteSubAccountController(DeleteSubAccountController c) { this.deleteSubAccountController = c; }
    public void setExchangeController(ExchangeController c) { this.exchangeController = c; }
    public void setSwitchExchangeController(SwitchExchangeController c) { this.switchExchangeController = c; }
    public void setSwitchTransferController(SwitchTransferController c) { this.switchTransferController = c; }
    public void setSwitchHistoryController(SwitchHistoryController c) { this.switchHistoryController = c; }
    public void setSwitchBuyAssetController(SwitchBuyAssetController c) { this.switchBuyAssetController = c; }
    public void setSwitchSellAssetController(SwitchSellAssetController c) { this.switchSellAssetController = c; }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();

        if ("state".equals(propName)) {
            LoggedInState state = (LoggedInState) evt.getNewValue();
            userLabel.setText("User: " + state.getUsername());
            refreshSubAccounts(state.getSubAccounts());
        }
        else if ("password".equals(propName)) {
            LoggedInState state = (LoggedInState) evt.getNewValue();
            if (state.getPasswordError() == null) {
                JOptionPane.showMessageDialog(this, "Password updated for " + state.getUsername());
            } else {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }
        else if ("subAccounts".equals(propName)) {
            LoggedInState state = loggedInViewModel.getState();
            refreshSubAccounts(state.getSubAccounts());
        }
        else if ("subAccountError".equals(propName)) {
            LoggedInState state = loggedInViewModel.getState();
            JOptionPane.showMessageDialog(this, state.getSubAccountError());
        }
        else if ("notification".equals(propName)) {
            String msg = (String) evt.getNewValue();
            Component parent = this.isShowing() ? this : null;
            JOptionPane.showMessageDialog(parent, msg, "Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshSubAccounts(java.util.List<SubAccount> subs) {
        for (int i = 0; i < MAX_SUBACCOUNTS; i++) {
            if (i < subs.size()) {
                SubAccount sa = subs.get(i);
                subAccountNameLabels[i].setText(sa.getName());
                subAccountCurrencyLabels[i].setText("Currency  USD: " + sa.getBalanceUSD());
                subAccountStockLabels[i].setText("Stock: (none)");
            } else {
                subAccountNameLabels[i].setText("Empty slot");
                subAccountCurrencyLabels[i].setText("Currency  USD: -");
                subAccountStockLabels[i].setText("Stock: (none)");
            }
        }
        revalidate();
        repaint();
    }

    public String getViewName() { return VIEW_NAME; }
    @Override public void actionPerformed(ActionEvent e) {}
}