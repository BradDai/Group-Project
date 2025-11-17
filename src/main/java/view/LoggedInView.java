package view;

import interface_adapter.exchange.ExchangeController;
import interface_adapter.logged_in.*;
import interface_adapter.logout.LogoutController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    public static final String VIEW_NAME = "logged in";

    private final LoggedInViewModel loggedInViewModel;

    private LogoutController logoutController;
    private ExchangeController exchangeController;
    private SwitchExchangeController switchExchangeController;
    private SwitchTransferController switchTransferController;
    private SwitchHistoryController switchHistoryController;
    private SwitchBuyAssetController switchBuyAssetController;
    private SwitchSellAssetController switchSellAssetController;


    // show user
    private final JLabel userLabel = new JLabel("User");

    // top
    private final JButton logoutButton = new JButton("Log out");
    private final JButton changePasswordButton = new JButton("Change Password");
    private final JButton createSubButton = new JButton("Create Subaccount");
    private final JButton deleteSubButton = new JButton("Delete Subaccount");

    private final JButton logOut;

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

        final JLabel title = new JLabel("Logged In Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel passwordInfo = new LabelTextPanel(
                new JLabel("Password"), passwordInputField);

        JPanel topPanel = new JPanel(new BorderLayout());

        final JPanel buttons = new JPanel();

        logOut = new JButton("Log Out");
        buttons.add(logOut);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        rightTop.add(logoutButton);
        rightTop.add(changePasswordButton);
        rightTop.add(createSubButton);
        rightTop.add(deleteSubButton);

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

                        this.logoutController.execute(
                        );
                    }
                }
        );

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JLabel nameLabel;
            JLabel balanceLabel;

            private void documentListenerHelper() {
                final LoggedInState currentState = loggedInViewModel.getState();
                currentState.setPassword(passwordInputField.getText());
                loggedInViewModel.setState(currentState);
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

        this.add(passwordInfo);
        this.add(passwordErrorField);
        this.add(buttons);
    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            username.setText(state.getUsername());
        }
        else if (evt.getPropertyName().equals("password")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            if (state.getPasswordError() == null) {
                JOptionPane.showMessageDialog(this, "password updated for " + state.getUsername());
                passwordInputField.setText("");
            }
            else {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Info", JOptionPane.INFORMATION_MESSAGE
        );
    }

    public String getViewName() {
        return viewName;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
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
