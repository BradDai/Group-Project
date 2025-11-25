package view;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import data_access.Constants;
import interface_adapter.SwitchLoggedInController;
import interface_adapter.transfer.TransferController;
import interface_adapter.transfer.TransferException;
import interface_adapter.transfer.TransferState;
import interface_adapter.transfer.TransferViewModel;
import view.components.BalancePanel;
import view.components.CurrencyPanel;
import view.components.StockPanel;
import view.components.TopPanel;
import view.components.TransferButtonPanel;

/**
 * Main Transfer view delegating UI components to sub-panels.
 */
public class TransferView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = Constants.VIEW_NAME;
    private final TransferViewModel transferViewModel;
    private SwitchLoggedInController switchLoggedInController;
    private TransferController transferController;

    // Sub-panels
    private final TopPanel topPanel;
    private final StockPanel stockPanel;
    private final CurrencyPanel currencyPanel;
    private final BalancePanel balancePanel;
    private final TransferButtonPanel buttonPanel;

    private boolean isUpdating;

    public TransferView(final TransferViewModel transferViewModel) {
        this.transferViewModel = transferViewModel;
        this.transferViewModel.addPropertyChangeListener(this);
        this.setName(viewName);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Initialize sub-panels
        topPanel = new TopPanel();
        stockPanel = new StockPanel();
        currencyPanel = new CurrencyPanel();
        balancePanel = new BalancePanel();
        buttonPanel = new TransferButtonPanel();

        // Add components to Layout
        this.add(topPanel);

        this.add(Box.createVerticalStrut(Constants.RIGID_AREA_SMALL));

        // Initially add balance panel to stock panel logic
        stockPanel.add(balancePanel, Constants.INDEX_SECOND);
        this.add(stockPanel);
        this.add(currencyPanel);

        this.add(Box.createVerticalStrut(Constants.RIGID_AREA_MEDIUM));

        this.add(buttonPanel);

        setupActionListeners();
        isUpdating = false;
    }

    private void setupActionListeners() {
        // Top Panel Listener (Portfolio or Type change)
        topPanel.addActionListener(evt -> {
            if (!isUpdating) {
                handleTopPanelChange();
            }
        });

        // Stock/Currency Listeners
        stockPanel.addActionListener(evt -> {
            if (!isUpdating) {
                triggerBalanceCheck();
            }
        });

        currencyPanel.addActionListener(evt -> {
            if (!isUpdating) {
                triggerBalanceCheck();
            }
        });

        // Validation Listener
        currencyPanel.addAmountChangeListener(() -> currencyPanel.validateAmount(buttonPanel));

        buttonPanel.addConfirmListener(this::actionPerformedConfirm);

        buttonPanel.addCancelListener(evt -> {
            if (switchLoggedInController != null) {
                switchLoggedInController.switchToLoggedInView();
            }
        });
    }

    private void handleTopPanelChange() {
        final String type = topPanel.getTransferType();

        if (Constants.TRANSFER_STOCK.equals(type)) {
            stockPanel.setVisible(true);
            currencyPanel.setVisible(false);

            // Update dropdowns immediately for the new type
            updateSubPanels(transferViewModel.getState());

            // Move balance panel
            stockPanel.add(balancePanel, Constants.INDEX_SECOND);
        }
        else {
            stockPanel.setVisible(false);
            currencyPanel.setVisible(true);

            updateSubPanels(transferViewModel.getState());

            // Move balance panel
            currencyPanel.add(balancePanel, Constants.INDEX_SECOND);
        }

        triggerBalanceCheck();
        this.revalidate();
        this.repaint();

        final Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.pack();
        }
    }

    private void triggerBalanceCheck() {
        if (transferController != null) {
            final TransferState state = transferViewModel.getState();
            String asset = "";

            if (stockPanel.isVisible()) {
                final String sel = stockPanel.getSelectedStock();
                // If sel is null, asset remains empty string, returning 0 balance.
                if (sel != null) {
                    asset = sel;
                }
            }
            else {
                final String sel = currencyPanel.getSelectedCurrency();
                if (sel != null) {
                    asset = sel;
                }
                else {
                    asset = Constants.DEFAULT_CURRENCY;
                }
            }

            transferController.checkBalances(
                state.getUsername(),
                topPanel.getFromPortfolio(),
                topPanel.getToPortfolio(),
                asset
            );
        }
    }

    private void executeTransfer() {
        if (transferController != null) {
            final TransferState state = transferViewModel.getState();
            final String username = state.getUsername();
            final String from = topPanel.getFromPortfolio();
            final String to = topPanel.getToPortfolio();
            try {
                if (stockPanel.isVisible()) {
                    // Execute Stock Transfer
                    transferController.executeStockTransfer(
                        username, from, to, stockPanel.getSelectedStock(), stockPanel.getAmount()
                    );
                }
                else {
                    // Execute Currency Transfer
                    transferController.executeCurrencyTransfer(
                        username, from, to, currencyPanel.getSelectedCurrency(), currencyPanel.getAmount()
                    );
                }
            }
            catch (final NumberFormatException numberFormatException) {
                currencyPanel.showError(Constants.ERROR_INVALID_AMOUNT);
            }
            catch (final TransferException transferException) {
                // Show the error message from the exception
                currencyPanel.showError(transferException.getMessage());
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        System.out.println("Click " + e.getActionCommand());
    }

    private void actionPerformedConfirm(final ActionEvent actionEvent) {
        executeTransfer();
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (Constants.STATE_PROPERTY.equals(evt.getPropertyName())) {
            processStateUpdate((TransferState) evt.getNewValue());
        }
        else if (Constants.ERROR_PROPERTY.equals(evt.getPropertyName())) {
            final TransferState state = (TransferState) evt.getNewValue();
            if (state.getError() != null && !state.getError().isEmpty()) {
                currencyPanel.showError(state.getError());
            }
        }
    }

    private void processStateUpdate(final TransferState state) {
        isUpdating = true;

        topPanel.updatePortfolios(state.getAvailablePortfolios());
        updateSubPanels(state);

        String typeLabel = Constants.LABEL_BALANCE;
        if (Constants.TRANSFER_STOCK.equals(topPanel.getTransferType())) {
            typeLabel = Constants.LABEL_QUANTITY_ONLY;
        }

        balancePanel.updateBalances(typeLabel, state.getFromBalance(), state.getToBalance());
        currencyPanel.setAmountText(state.getAmount());

        isUpdating = false;
        this.repaint();
    }

    private void updateSubPanels(final TransferState state) {
        stockPanel.setAvailableStocks(state.getAvailableStocks());
        currencyPanel.setAvailableCurrencies(state.getAvailableCurrencies());
    }

    public String getViewName() {
        return viewName;
    }

    public void setSwitchLoggedInController(final SwitchLoggedInController controller) {
        this.switchLoggedInController = controller;
    }

    public void setTransferController(final TransferController controller) {
        this.transferController = controller;
    }
}
