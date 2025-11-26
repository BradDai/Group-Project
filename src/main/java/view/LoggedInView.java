package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import entity.Asset;
import entity.SubAccount;
import interfaceadapter.exchange.ExchangeController;
import interfaceadapter.logged_in.ChangePasswordController;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.logged_in.SwitchBuyAssetController;
import interfaceadapter.logged_in.SwitchExchangeController;
import interfaceadapter.logged_in.SwitchHistoryController;
import interfaceadapter.logged_in.SwitchSellAssetController;
import interfaceadapter.logged_in.SwitchTransferController;
import interfaceadapter.logout.LogoutController;
import interfaceadapter.subaccount.create.CreateSubAccountController;
import interfaceadapter.subaccount.delete.DeleteSubAccountController;

public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    public static final String VIEW_NAME = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private ChangePasswordController changePasswordController;
    private CreateSubAccountController createSubAccountController;
    private LogoutController logoutController;
    private SwitchExchangeController switchExchangeController;
    private SwitchTransferController switchTransferController;
    private SwitchHistoryController switchHistoryController;
    private SwitchBuyAssetController switchBuyAssetController;
    private SwitchSellAssetController switchSellAssetController;
    private DeleteSubAccountController deleteSubAccountController;

    private final JLabel userLabel = new JLabel("User");

    private static final int MAX_SUBACCOUNTS = 5;
    private final JLabel[] subAccountNameLabels = new JLabel[MAX_SUBACCOUNTS];
    private final JLabel[] subAccountCurrencyLabels = new JLabel[MAX_SUBACCOUNTS];
    private final JLabel[] subAccountStockLabels = new JLabel[MAX_SUBACCOUNTS];

    public LoggedInView(final LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final JPanel topPanel = new JPanel(new BorderLayout());
        final JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftTop.add(userLabel);
        final JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        final JButton logoutButton = new JButton("Log out");
        rightTop.add(logoutButton);
        final JButton changePasswordButton = new JButton("Change Password");
        rightTop.add(changePasswordButton);
        final JButton createSubButton = new JButton("Create Subaccount");
        rightTop.add(createSubButton);
        final JButton deleteSubButton = new JButton("Delete Subaccount");
        rightTop.add(deleteSubButton);
        topPanel.add(leftTop, BorderLayout.WEST);
        topPanel.add(rightTop, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        final JPanel centerPanel = new JPanel(new BorderLayout());
        final JPanel accountsRow = new JPanel(new GridLayout(1, MAX_SUBACCOUNTS, 10, 0));
        for (int i = 0; i < MAX_SUBACCOUNTS; i++) {
            final JPanel slot = new JPanel();
            slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));
            slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            final JLabel nameLabel = new JLabel();
            final JLabel currencyLabel = new JLabel();
            final JLabel stockLabel = new JLabel();
            if (i == 0) {
                nameLabel.setText("Main USD Portfolio");
                currencyLabel.setText("Currency  USD: 1,000,000.00");
                stockLabel.setText("Stock: (none)");
            }
            else {
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
            final JPanel[] subAccountPanels = new JPanel[MAX_SUBACCOUNTS];
            subAccountPanels[i] = slot;
        }
        centerPanel.add(accountsRow, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        final JPanel bottomPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        final JButton buyAssetButton = new JButton("Buy Asset");
        bottomPanel.add(buyAssetButton);
        final JButton sellAssetButton = new JButton("Sell Asset");
        bottomPanel.add(sellAssetButton);
        final JButton convertCurrencyButton = new JButton("Convert Currency");
        bottomPanel.add(convertCurrencyButton);
        final JButton transferMoneyButton = new JButton("Transfer Money");
        bottomPanel.add(transferMoneyButton);
        final JButton historyButton = new JButton("Show Transaction History");
        bottomPanel.add(historyButton);
        bottomPanel.add(new JLabel());
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        logoutButton.addActionListener(actionEvent -> {
            if (logoutController != null) {
                logoutController.execute();
            }
        });

        changePasswordButton.addActionListener(actionEvent -> {
            if (changePasswordController != null) {
                final String username = loggedInViewModel.getState().getUsername();
                final String newPassword =
                    JOptionPane.showInputDialog(this, "Enter new password:", "Change Password",
                        JOptionPane.PLAIN_MESSAGE);
                if (newPassword != null && !newPassword.isEmpty()) {
                    changePasswordController.execute(username, newPassword);
                }
            }
        });

        createSubButton.addActionListener(actionEvent -> {
            if (createSubAccountController != null) {
                final String name = JOptionPane.showInputDialog(this, "Enter new subaccount name:", "Create Subaccount",
                    JOptionPane.PLAIN_MESSAGE);
                if (name != null && !name.isBlank()) {
                    createSubAccountController.execute(loggedInViewModel.getState().getUsername(), name.trim());
                }
            }
        });

        deleteSubButton.addActionListener(actionEvent -> {
            if (deleteSubAccountController != null) {
                final String name =
                    JOptionPane.showInputDialog(this, "Enter subaccount name to delete:", "Delete Subaccount",
                        JOptionPane.PLAIN_MESSAGE);
                if (name != null && !name.isBlank()) {
                    deleteSubAccountController.execute(loggedInViewModel.getState().getUsername(), name.trim());
                }
            }
        });

        convertCurrencyButton.addActionListener(actionEvent -> {
            if (switchExchangeController != null) {
                final String username = loggedInViewModel.getState().getUsername();
                switchExchangeController.switchToExchangeView(username);
            }
        });

        transferMoneyButton.addActionListener(actionEvent -> {
            if (switchTransferController != null) {
                final String username = loggedInViewModel.getState().getUsername();
                switchTransferController.switchToTransferView(username);
            }
        });

        historyButton.addActionListener(actionEvent -> {
            if (switchHistoryController != null) {
                switchHistoryController.switchToHistoryView();
            }
        });
        buyAssetButton.addActionListener(actionEvent -> {
            if (switchBuyAssetController != null) {
                switchBuyAssetController.switchToBuyAssetView();
            }
        });
        sellAssetButton.addActionListener(actionEvent -> {
            if (switchSellAssetController != null) {
                final String username = loggedInViewModel.getState().getUsername();
                switchSellAssetController.switchToSellAssetView(username);
            }
        });
    }

    // Setters
    public void setLogoutController(final LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setChangePasswordController(final ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setCreateSubAccountController(final CreateSubAccountController createSubAccountController) {
        this.createSubAccountController = createSubAccountController;
    }

    public void setDeleteSubAccountController(final DeleteSubAccountController deleteSubAccountController) {
        this.deleteSubAccountController = deleteSubAccountController;
    }

    public void setExchangeController(final ExchangeController exchangeController) {
    }

    public void setSwitchExchangeController(final SwitchExchangeController switchExchangeController) {
        this.switchExchangeController = switchExchangeController;
    }

    public void setSwitchTransferController(final SwitchTransferController switchTransferController) {
        this.switchTransferController = switchTransferController;
    }

    public void setSwitchHistoryController(final SwitchHistoryController switchHistoryController) {
        this.switchHistoryController = switchHistoryController;
    }

    public void setSwitchBuyAssetController(final SwitchBuyAssetController switchBuyAssetController) {
        this.switchBuyAssetController = switchBuyAssetController;
    }

    public void setSwitchSellAssetController(final SwitchSellAssetController switchSellAssetController) {
        this.switchSellAssetController = switchSellAssetController;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final String propName = evt.getPropertyName();

        if ("state".equals(propName)) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            userLabel.setText("User: " + state.getUsername());
            refreshSubAccounts(state.getSubAccounts());
        }
        else if ("password".equals(propName)) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            if (state.getPasswordError() == null) {
                JOptionPane.showMessageDialog(this, "Password updated for " + state.getUsername());
            }
            else {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }
        else if ("subAccounts".equals(propName)) {
            final LoggedInState state = loggedInViewModel.getState();
            refreshSubAccounts(state.getSubAccounts());
        }
        else if ("subAccountError".equals(propName)) {
            final LoggedInState state = loggedInViewModel.getState();
            JOptionPane.showMessageDialog(this, state.getSubAccountError());
        }
        else if ("notification".equals(propName)) {
            final String msg = (String) evt.getNewValue();
            final Component parent;
            if (this.isShowing()) {
                parent = this;
            }
            else {
                parent = null;
            }
            JOptionPane.showMessageDialog(parent, msg, "Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshSubAccounts(final java.util.List<SubAccount> subs) {
        for (int i = 0; i < MAX_SUBACCOUNTS; i++) {
            if (i < subs.size()) {
                final SubAccount sa = subs.get(i);
                subAccountNameLabels[i].setText(sa.getName());
                subAccountCurrencyLabels[i].setText(formatCurrenciesForLabel(sa));
                subAccountStockLabels[i].setText(formatStocksForLabel(sa));
            }
            else {
                subAccountNameLabels[i].setText("Empty slot");
                subAccountCurrencyLabels[i].setText("Currency  USD: -");
                subAccountStockLabels[i].setText("Stock: (none)");
            }
        }
        revalidate();
        repaint();
    }

    private String formatCurrenciesForLabel(final SubAccount subAccount) {
        String result = "Currency  (none)";
        final Map<String, java.math.BigDecimal> currencies = subAccount.getCurrencies();
        if (currencies != null && !currencies.isEmpty()) {
            final StringBuilder sb = new StringBuilder("<html>Currency&nbsp;&nbsp;");
            boolean first = true;
            for (final Map.Entry<String, java.math.BigDecimal> entry : currencies.entrySet()) {
                if (!first) {
                    sb.append("<br>");
                }
                first = false;
                sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue().toPlainString());
            }
            sb.append("</html>");
            result = sb.toString();
        }
        return result;
    }

    private String formatStocksForLabel(final SubAccount subAccount) {
        String result = "<html>Stock: (none)</html>";
        final List<Asset> assets = subAccount.getAssets();
        if (assets != null && !assets.isEmpty()) {
            final StringBuilder sb = new StringBuilder("<html>Stock:<br>");
            for (final Asset a : assets) {
                sb.append("&nbsp;&nbsp;")
                    .append(a.getType())
                    .append(": ")
                    .append(a.getQuantity())
                    .append("<br>");
            }
            sb.append("</html>");
            result = sb.toString();
        }
        return result;
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
    }
}
