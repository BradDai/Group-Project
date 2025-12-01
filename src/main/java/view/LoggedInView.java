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
import java.math.BigDecimal;
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

/**
 * View shown after a user has logged in.
 */
public class LoggedInView extends JPanel
        implements ActionListener, PropertyChangeListener {

    public static final String VIEW_NAME = "logged in";

    private static final int MAX_SUBACCOUNTS = 5;

    // layout / spacing constants
    private static final int GAP_SMALL = 5;
    private static final int GAP_MEDIUM = 10;
    private static final int BORDER_SIZE = 2;
    private static final int GRID_ROWS_SINGLE = 1;
    private static final int GRID_ROWS_DOUBLE = 2;
    private static final int GRID_COLS = 3;

    // repeated strings
    private static final String STOCK_NONE = "Stock: (none)";
    private static final String CURRENCY_NONE = "Currency  (none)";
    private static final String EMPTY_SLOT = "Empty slot";
    private static final String MAIN_PORTFOLIO_NAME = "Main USD Portfolio";

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

    private final JLabel[] subAccountNameLabels =
            new JLabel[MAX_SUBACCOUNTS];
    private final JLabel[] subAccountCurrencyLabels =
            new JLabel[MAX_SUBACCOUNTS];
    private final JLabel[] subAccountStockLabels =
            new JLabel[MAX_SUBACCOUNTS];

    /**
     * Creates a new logged in view.
     *
     * @param loggedInViewModel the view model backing this view
     */
    public LoggedInView(final LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(GAP_MEDIUM, GAP_MEDIUM));
        setBorder(
                BorderFactory.createEmptyBorder(
                        GAP_MEDIUM, GAP_MEDIUM, GAP_MEDIUM, GAP_MEDIUM));
        buildTopPanel();
        buildCenterPanel();
        buildBottomPanel();
    }

    private void buildTopPanel() {
        final JPanel topPanel = new JPanel(new BorderLayout());
        final JPanel leftTop =
                new JPanel(
                        new FlowLayout(FlowLayout.LEFT,
                                GAP_MEDIUM, GAP_SMALL));
        leftTop.add(userLabel);

        final JPanel rightTop =
                new JPanel(
                        new FlowLayout(FlowLayout.LEFT,
                                GAP_MEDIUM, GAP_SMALL));

        final JButton logoutButton = new JButton("Log out");
        final JButton changePasswordButton =
                new JButton("Change Password");
        final JButton createSubButton =
                new JButton("Create Subaccount");
        final JButton deleteSubButton =
                new JButton("Delete Subaccount");

        rightTop.add(logoutButton);
        rightTop.add(changePasswordButton);
        rightTop.add(createSubButton);
        rightTop.add(deleteSubButton);

        topPanel.add(leftTop, BorderLayout.WEST);
        topPanel.add(rightTop, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        logoutButton.addActionListener(this::handleLogout);
        changePasswordButton.addActionListener(
                this::handleChangePassword);
        createSubButton.addActionListener(
                this::handleCreateSubAccount);
        deleteSubButton.addActionListener(
                this::handleDeleteSubAccount);
    }

    private void buildCenterPanel() {
        final JPanel centerPanel = new JPanel(new BorderLayout());
        final JPanel accountsRow =
                new JPanel(
                        new GridLayout(
                                GRID_ROWS_SINGLE,
                                MAX_SUBACCOUNTS,
                                GAP_MEDIUM, 0));

        for (int i = 0; i < MAX_SUBACCOUNTS; i++) {
            final JPanel slot = new JPanel();
            slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));
            slot.setBorder(
                    BorderFactory.createLineBorder(
                            Color.BLACK, BORDER_SIZE));

            final JLabel nameLabel = new JLabel();
            final JLabel currencyLabel = new JLabel();
            final JLabel stockLabel = new JLabel();

            if (i == 0) {
                nameLabel.setText(MAIN_PORTFOLIO_NAME);
                currencyLabel.setText("Currency  USD: 1,000,000.00");
                stockLabel.setText(STOCK_NONE);
            }
            else {
                nameLabel.setText(EMPTY_SLOT);
                currencyLabel.setText("Currency  USD: -");
                stockLabel.setText(STOCK_NONE);
            }

            subAccountNameLabels[i] = nameLabel;
            subAccountCurrencyLabels[i] = currencyLabel;
            subAccountStockLabels[i] = stockLabel;
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            currencyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            slot.add(Box.createVerticalStrut(GAP_SMALL));
            slot.add(nameLabel);
            slot.add(Box.createVerticalStrut(GAP_MEDIUM));
            slot.add(currencyLabel);
            slot.add(Box.createVerticalStrut(GAP_SMALL));
            slot.add(stockLabel);
            slot.add(Box.createVerticalGlue());
            accountsRow.add(slot);
        }

        centerPanel.add(accountsRow, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void buildBottomPanel() {
        final JPanel bottomPanel =
                new JPanel(
                        new GridLayout(
                                GRID_ROWS_DOUBLE,
                                GRID_COLS,
                                GAP_MEDIUM, GAP_MEDIUM));

        final JButton buyAssetButton = new JButton("Buy Asset");
        final JButton sellAssetButton = new JButton("Sell Asset");
        final JButton convertCurrencyButton =
                new JButton("Convert Currency");
        final JButton transferMoneyButton =
                new JButton("Transfer Money");
        final JButton historyButton =
                new JButton("Show Transaction History");

        bottomPanel.add(buyAssetButton);
        bottomPanel.add(sellAssetButton);
        bottomPanel.add(convertCurrencyButton);
        bottomPanel.add(transferMoneyButton);
        bottomPanel.add(historyButton);
        bottomPanel.add(new JLabel());

        add(bottomPanel, BorderLayout.SOUTH);

        buyAssetButton.addActionListener(this::handleBuyAsset);
        sellAssetButton.addActionListener(this::handleSellAsset);
        convertCurrencyButton.addActionListener(
                this::handleConvertCurrency);
        transferMoneyButton.addActionListener(
                this::handleTransferMoney);
        historyButton.addActionListener(this::handleShowHistory);
    }

    private void handleLogout(final ActionEvent event) {
        if (logoutController != null) {
            logoutController.execute();
        }
    }

    private void handleChangePassword(final ActionEvent event) {
        if (changePasswordController != null) {
            final String username =
                    loggedInViewModel.getState().getUsername();
            final String newPassword =
                    JOptionPane.showInputDialog(
                            this,
                            "Enter new password:",
                            "Change Password",
                            JOptionPane.PLAIN_MESSAGE);
            if (newPassword != null && !newPassword.isEmpty()) {
                changePasswordController.execute(
                        username, newPassword);
            }
        }
    }

    private void handleCreateSubAccount(final ActionEvent event) {
        if (createSubAccountController != null) {
            final String name =
                    JOptionPane.showInputDialog(
                            this,
                            "Enter new subaccount name:",
                            "Create Subaccount",
                            JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                createSubAccountController.execute(
                        loggedInViewModel.getState().getUsername(),
                        name.trim());
            }
        }
    }

    private void handleDeleteSubAccount(final ActionEvent event) {
        if (deleteSubAccountController != null) {
            final String name =
                    JOptionPane.showInputDialog(
                            this,
                            "Enter subaccount name to delete:",
                            "Delete Subaccount",
                            JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                deleteSubAccountController.execute(
                        loggedInViewModel.getState().getUsername(),
                        name.trim());
            }
        }
    }

    private void handleConvertCurrency(final ActionEvent event) {
        if (switchExchangeController != null) {
            final String username =
                    loggedInViewModel.getState().getUsername();
            switchExchangeController.switchToExchangeView(username);
        }
    }

    private void handleTransferMoney(final ActionEvent event) {
        if (switchTransferController != null) {
            final String username =
                    loggedInViewModel.getState().getUsername();
            switchTransferController.switchToTransferView(username);
        }
    }

    private void handleShowHistory(final ActionEvent event) {
        if (switchHistoryController != null) {
            switchHistoryController.switchToHistoryView();
        }
    }

    private void handleBuyAsset(final ActionEvent event) {
        if (switchBuyAssetController != null) {
            switchBuyAssetController.switchToBuyAssetView();
        }
    }

    private void handleSellAsset(final ActionEvent event) {
        if (switchSellAssetController != null) {
            final String username =
                    loggedInViewModel.getState().getUsername();
            switchSellAssetController.switchToSellAssetView(username);
        }
    }

    // ======== Controller setters ========

    public void setLogoutController(
            final LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setChangePasswordController(
            final ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setCreateSubAccountController(
            final CreateSubAccountController createSubAccountController) {
        this.createSubAccountController = createSubAccountController;
    }

    public void setDeleteSubAccountController(
            final DeleteSubAccountController deleteSubAccountController) {
        this.deleteSubAccountController = deleteSubAccountController;
    }

    /**
     * Unused, kept for compatibility with the example code.
     *
     * @param exchangeController ignored
     */
    public void setExchangeController(
            final ExchangeController exchangeController) {
        // no-op
    }

    public void setSwitchExchangeController(
            final SwitchExchangeController switchExchangeController) {
        this.switchExchangeController = switchExchangeController;
    }

    public void setSwitchTransferController(
            final SwitchTransferController switchTransferController) {
        this.switchTransferController = switchTransferController;
    }

    public void setSwitchHistoryController(
            final SwitchHistoryController switchHistoryController) {
        this.switchHistoryController = switchHistoryController;
    }

    public void setSwitchBuyAssetController(
            final SwitchBuyAssetController switchBuyAssetController) {
        this.switchBuyAssetController = switchBuyAssetController;
    }

    public void setSwitchSellAssetController(
            final SwitchSellAssetController switchSellAssetController) {
        this.switchSellAssetController = switchSellAssetController;
    }

    // ======== PropertyChangeListener ========

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final String propName = evt.getPropertyName();

        if ("state".equals(propName)
                || "subAccounts".equals(propName)) {

            final LoggedInState state =
                    (LoggedInState) evt.getNewValue();
            userLabel.setText("User: " + state.getUsername());
            refreshSubAccounts(state.getSubAccounts());
        }
        else if ("password".equals(propName)) {
            handlePasswordPropertyChange(
                    (LoggedInState) evt.getNewValue());
        }
        else if ("subAccountError".equals(propName)) {
            final LoggedInState state =
                    loggedInViewModel.getState();
            JOptionPane.showMessageDialog(
                    this, state.getSubAccountError());
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
            JOptionPane.showMessageDialog(
                    parent, msg,
                    "Notification",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handlePasswordPropertyChange(
            final LoggedInState state) {

        if (state.getPasswordError() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Password updated for "
                            + state.getUsername());
        }
        else {
            JOptionPane.showMessageDialog(
                    this, state.getPasswordError());
        }
    }

    private void refreshSubAccounts(final List<SubAccount> subs) {
        for (int i = 0; i < MAX_SUBACCOUNTS; i++) {
            if (i < subs.size()) {
                final SubAccount sa = subs.get(i);
                subAccountNameLabels[i].setText(sa.getName());
                subAccountCurrencyLabels[i].setText(
                        formatCurrenciesForLabel(sa));
                subAccountStockLabels[i].setText(
                        formatStocksForLabel(sa));
            }
            else {
                subAccountNameLabels[i].setText(EMPTY_SLOT);
                subAccountCurrencyLabels[i].setText(
                        "Currency  USD: -");
                subAccountStockLabels[i].setText(STOCK_NONE);
            }
        }
        revalidate();
        repaint();
    }

    private String formatCurrenciesForLabel(
            final SubAccount subAccount) {

        String result = CURRENCY_NONE;
        final Map<String, BigDecimal> currencies =
                subAccount.getCurrencies();
        if (currencies != null && !currencies.isEmpty()) {
            final StringBuilder sb =
                    new StringBuilder("<html>Currency&nbsp;&nbsp;");
            boolean first = true;
            for (final Map.Entry<String, BigDecimal> entry
                    : currencies.entrySet()) {
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

    private String formatStocksForLabel(
            final SubAccount subAccount) {

        String result = "<html>" + STOCK_NONE + "</html>";
        final List<Asset> assets = subAccount.getAssets();
        if (assets != null && !assets.isEmpty()) {
            final StringBuilder sb =
                    new StringBuilder("<html>Stock:<br>");
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

    /**
     * Returns the name used by the ViewManager
     * to identify this view.
     *
     * @return the view name
     */
    public String getViewName() {
        return VIEW_NAME;
    }

    /**
     * Required override for {@link ActionListener}.
     * Not used because we attach specific handlers
     * via method references.
     *
     * @param event the action event
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        // no-op
    }
}