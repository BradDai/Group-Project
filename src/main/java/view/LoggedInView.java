package view;

import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.exchange.ExchangeController;
import interface_adapter.logged_in.*;
import interface_adapter.logout.LogoutController;
import interface_adapter.logged_in.ChangePasswordController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoggedInView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "logged in";

    private final LoggedInViewModel loggedInViewModel;

    private LogoutController logoutController;
    private ChangePasswordController changePasswordController;

    private final JLabel userLabel = new JLabel("User");
    private ExchangeController exchangeController;
    private SwitchExchangeController switchExchangeController;
    private SwitchTransferController switchTransferController;
    private SwitchHistoryController switchHistoryController;
    private SwitchBuyAssetController switchBuyAssetController;
    private SwitchSellAssetController switchSellAssetController;

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
    private final JLabel[] subAccountBalanceLabels = new JLabel[MAX_SUBACCOUNTS];
    private final JTextField passwordInputField = new JTextField(15);
    private final JButton changePassword;
    private final JButton currencyExchange;
    private final JButton transfer;
    private final JButton history;
    private final JButton buyAsset;
    private final JButton sellAsset;

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftTop.add(userLabel);
        final JPanel buttons = new JPanel();

        logOut = new JButton("Log Out");
        buttons.add(logOut);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        rightTop.add(logoutButton);
        rightTop.add(changePasswordButton);
        rightTop.add(createSubButton);
        rightTop.add(deleteSubButton);

        topPanel.add(leftTop, BorderLayout.WEST);
        topPanel.add(rightTop, BorderLayout.CENTER);
        currencyExchange = new JButton("Currency Exchange");
        buttons.add(currencyExchange);

        transfer = new JButton("Transfer Between Accounts");
        buttons.add(transfer);

        history = new JButton("Transaction History");
        buttons.add(history);

        buyAsset = new JButton("Buy Asset");
        buttons.add(buyAsset);

        sellAsset = new JButton("Sell Asset");
        buttons.add(sellAsset);

        logOut.addActionListener(
                // This creates an anonymous subclass of ActionListener and instantiates it.
                evt -> {
                    if (evt.getSource().equals(logOut)) {
                        final LoggedInState currentState = loggedInViewModel.getState();

        add(topPanel, BorderLayout.NORTH);


        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel accountsRow = new JPanel(new GridLayout(1, MAX_SUBACCOUNTS, 10, 0));

        for (int i = 0; i < MAX_SUBACCOUNTS; i++) {
            JPanel slot = new JPanel();
            slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));
            slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JLabel nameLabel;
            JLabel balanceLabel;

            if (i == 0) {
                nameLabel = new JLabel("Main CAD Portfolio");
                balanceLabel = new JLabel("CAD: $1,000,000.00");
            } else {
                nameLabel = new JLabel("Empty slot");
                balanceLabel = new JLabel("CAD: -");
            }

            subAccountNameLabels[i] = nameLabel;
            subAccountBalanceLabels[i] = balanceLabel;

            slot.add(Box.createVerticalStrut(5));
            slot.add(nameLabel);
            slot.add(Box.createVerticalStrut(5));
            slot.add(balanceLabel);
            slot.add(Box.createVerticalGlue());

            accountsRow.add(slot);
            subAccountPanels[i] = slot;
        }

        centerPanel.add(accountsRow, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        currencyExchange.addActionListener(
                evt -> {
                    if (evt.getSource().equals(currencyExchange)) {

                        switchExchangeController.switchToExchangeView();
                        //exchangeController.execute();

                    }
                }
        );

        transfer.addActionListener(
                evt -> {
                    if (evt.getSource().equals(transfer)) {
                        switchTransferController.switchToTransferView();
                    }
                }
        );

        history.addActionListener(
                evt -> {
                    if (evt.getSource().equals(history)) {
                        switchHistoryController.switchToHistoryView();
                    }
                }
        );

        buyAsset.addActionListener(
                evt-> {
                    if (evt.getSource().equals(buyAsset)) {
                        switchBuyAssetController.switchToBuyAssetView();
                    }
                }
        );

        sellAsset.addActionListener(
                evt -> {
                    if (evt.getSource().equals(sellAsset)) {
                        switchSellAssetController.switchToSellAssetView();
                    }
                }
        );

        this.add(title);
        this.add(usernameInfo);
        this.add(username);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        bottomPanel.add(buyAssetButton);
        bottomPanel.add(sellAssetButton);
        bottomPanel.add(convertCurrencyButton);

        bottomPanel.add(transferMoneyButton);
        bottomPanel.add(historyButton);
        bottomPanel.add(new JLabel());

        add(bottomPanel, BorderLayout.SOUTH);


    //button
        // Logout
        logoutButton.addActionListener(e -> {
            if (logoutController != null) {
                logoutController.execute();
            } else {
                showInfo("Logout controller not set yet.");
            }
        });

        // later Use Case
        changePasswordButton.addActionListener(e -> showInfo("Change password not implemented yet."));
        createSubButton.addActionListener(e -> showInfo("Create subaccount not implemented yet."));
        deleteSubButton.addActionListener(e -> showInfo("Delete subaccount not implemented yet."));
        buyAssetButton.addActionListener(e -> showInfo("Buy asset not implemented yet."));
        sellAssetButton.addActionListener(e -> showInfo("Sell asset not implemented yet."));
        convertCurrencyButton.addActionListener(e -> showInfo("Convert currency not implemented yet."));
        transferMoneyButton.addActionListener(e -> showInfo("Transfer money not implemented yet."));
        historyButton.addActionListener(e -> showInfo("Transaction history not implemented yet."));
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Info", JOptionPane.INFORMATION_MESSAGE
        );
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
    public void setLogoutController(LogoutController logoutController) {

        this.logoutController = logoutController;
    }

    public void setExchangeController(ExchangeController exchangeController) {
        this.exchangeController = exchangeController;
    }

    public void setSwitchExchangeController(SwitchExchangeController switchExchangeController) {
        this.switchExchangeController = switchExchangeController;
    }

    public void setSwitchTransferController(SwitchTransferController switchTransferController) {
        this.switchTransferController = switchTransferController;
    }

    public void setSwitchHistoryController(SwitchHistoryController switchHistoryController) {
        this.switchHistoryController = switchHistoryController;
    }

    public void setSwitchBuyAssetController(SwitchBuyAssetController switchBuyAssetController) {
        this.switchBuyAssetController = switchBuyAssetController;
    }

    public void setSwitchSellAssetController(SwitchSellAssetController switchSellAssetController) {
        this.switchSellAssetController = switchSellAssetController;
    }
}
