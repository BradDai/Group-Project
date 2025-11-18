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

    // show user
    private final JLabel userLabel = new JLabel("User");

    // top
    private final JButton logoutButton = new JButton("Log out");
    private final JButton changePasswordButton = new JButton("Change Password");
    private final JButton createSubButton = new JButton("Create Subaccount");
    private final JButton deleteSubButton = new JButton("Delete Subaccount");

    // bottom
    private final JButton buyAssetButton = new JButton("Buy Asset");
    private final JButton sellAssetButton = new JButton("Sell Asset");
    private final JButton convertCurrencyButton = new JButton("Convert Currency");
    private final JButton transferMoneyButton = new JButton("Transfer Money");
    private final JButton historyButton = new JButton("Show Transaction History");

    // Subaccount UI
    private static final int MAX_SUBACCOUNTS = 5;
    private final JPanel[] subAccountPanels = new JPanel[MAX_SUBACCOUNTS];
    private final JLabel[] subAccountNameLabels = new JLabel[MAX_SUBACCOUNTS];
    private final JLabel[] subAccountBalanceLabels = new JLabel[MAX_SUBACCOUNTS];
    private final JTextArea[] subAccountAssetsAreas = new JTextArea[MAX_SUBACCOUNTS];

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
            JLabel nameLabel;
            JLabel balanceLabel;
            if (i == 0) {
                nameLabel = new JLabel("Main USD Portfolio");
                balanceLabel = new JLabel("USD: $1,000,000.00");
            } else {
                nameLabel = new JLabel("Empty slot");
                balanceLabel = new JLabel("USD: -");
            }

            subAccountNameLabels[i] = nameLabel;
            subAccountBalanceLabels[i] = balanceLabel;
            JTextArea assetArea = new JTextArea();
            assetArea.setEditable(false);
            assetArea.setLineWrap(true);
            assetArea.setWrapStyleWord(true);
            assetArea.setOpaque(false);
            assetArea.setBorder(null);
            assetArea.setText("Assets: (none)");
            subAccountAssetsAreas[i] = assetArea;
            slot.add(Box.createVerticalStrut(5));
            slot.add(nameLabel);
            slot.add(Box.createVerticalStrut(5));
            slot.add(balanceLabel);
            slot.add(Box.createVerticalStrut(5));
            slot.add(assetArea);
            slot.add(Box.createVerticalGlue());
            accountsRow.add(slot);
            subAccountPanels[i] = slot;
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            assetArea.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        // button
        logoutButton.addActionListener(e -> {
            if (logoutController != null) {
                logoutController.execute();
            } else {
                showInfo("Logout controller not set yet.");
            }
        });

        changePasswordButton.addActionListener(e -> {
            if (changePasswordController == null) {
                showInfo("ChangePassword controller not set yet.");
                return;
            }
            LoggedInState state = loggedInViewModel.getState();
            String username = state.getUsername();

            String newPassword = JOptionPane.showInputDialog(
                    this,
                    "Enter new password for " + username + ":",
                    "Change Password",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (newPassword != null && !newPassword.isEmpty()) {
                changePasswordController.execute(username, newPassword);
            }
        });

        createSubButton.addActionListener(e -> {
            if (createSubAccountController == null) {
                showInfo("CreateSubAccount controller not set yet.");
                return;
            }

            String name = JOptionPane.showInputDialog(
                    this,
                    "Enter new subaccount name:",
                    "Create Subaccount",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (name != null && !name.isBlank()) {
                String username = loggedInViewModel.getState().getUsername();
                createSubAccountController.execute(username, name.trim());
            }
        });

        deleteSubButton.addActionListener(e -> {
            if (deleteSubAccountController == null) {
                showInfo("DeleteSubAccount controller not set yet.");
                return;
            }
            String name = JOptionPane.showInputDialog(
                    this,
                    "Enter subaccount name to delete:",
                    "Delete Subaccount",
                    JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                String username = loggedInViewModel.getState().getUsername();
                deleteSubAccountController.execute(username, name.trim());
            }

        });

        // TODO: Currency Exchange
        convertCurrencyButton.addActionListener(e -> {
            if (switchExchangeController != null) {
                switchExchangeController.switchToExchangeView();
            } else {
                showInfo("Exchange view controller not set yet.");
            }
        });

        // TODO: Transfer
        transferMoneyButton.addActionListener(e -> {
            if (switchTransferController != null) {
                switchTransferController.switchToTransferView();
            } else {
                showInfo("Transfer view controller not set yet.");
            }
        });

        // TODO: History
        historyButton.addActionListener(e -> {
            if (switchHistoryController != null) {
                switchHistoryController.switchToHistoryView();
            } else {
                showInfo("History view controller not set yet.");
            }
        });

        // TODO: Buy Asset
        buyAssetButton.addActionListener(e -> {
            if (switchBuyAssetController != null) {
                switchBuyAssetController.switchToBuyAssetView();
            } else {
                showInfo("Buy Asset view controller not set yet.");
            }
        });

        // TODO: Sell Asset
        sellAssetButton.addActionListener(e -> {
            if (switchSellAssetController != null) {
                switchSellAssetController.switchToSellAssetView();
            } else {
                showInfo("Sell Asset view controller not set yet.");
            }
        });
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

    public void setCreateSubAccountController(CreateSubAccountController controller) {
        this.createSubAccountController = controller;
    }

    public void setDeleteSubAccountController(DeleteSubAccountController controller) {
        this.deleteSubAccountController = controller;
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
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            LoggedInState state = (LoggedInState) evt.getNewValue();
            userLabel.setText("User: " + state.getUsername());
            refreshSubAccounts(state.getSubAccounts());
        }
        else if ("password".equals(evt.getPropertyName())) {
            LoggedInState state = (LoggedInState) evt.getNewValue();
            if (state.getPasswordError() == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password updated for " + state.getUsername()
                );
            } else {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }
        else if ("subAccounts".equals(evt.getPropertyName())) {
            LoggedInState state = loggedInViewModel.getState();
            refreshSubAccounts(state.getSubAccounts());
        }
        else if ("subAccountError".equals(evt.getPropertyName())) {
            LoggedInState state = loggedInViewModel.getState();
            JOptionPane.showMessageDialog(this, state.getSubAccountError());
        }
    }
    private void refreshSubAccounts(java.util.List<SubAccount> subs) {

        for (int i = 0; i < MAX_SUBACCOUNTS; i++) {
            if (i < subs.size()) {
                SubAccount sa = subs.get(i);
                subAccountNameLabels[i].setText(sa.getName());
                subAccountBalanceLabels[i].setText("USD: $" + sa.getBalanceUSD());
                subAccountAssetsAreas[i].setText(formatAssets(sa.getAssets()));
            } else {
                subAccountNameLabels[i].setText("Empty slot");
                subAccountBalanceLabels[i].setText("USD: -");
                subAccountAssetsAreas[i].setText("Assets: (none)");
            }
        }
        revalidate();
        repaint();
    }
    private String formatAssets(java.util.List<Asset> assets) {
        if (assets == null || assets.isEmpty()) {
            return "Assets: (none)";
        }
        StringBuilder sb = new StringBuilder("Assets:\n");
        for (Asset a : assets) {
            sb.append("- ")
                    .append(a.getType())
                    .append(" x ")
                    .append(a.getQuantity())
                    .append("\n");
        }
        return sb.toString();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Click " + e.getActionCommand());
    }
}