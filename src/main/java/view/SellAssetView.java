package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.sell_asset.SellAssetController;
import interface_adapter.sell_asset.SellAssetState;
import interface_adapter.sell_asset.SellAssetViewModel;

public class SellAssetView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "sellasset";
    private final SellAssetViewModel sellAssetViewModel;
    private SwitchLoggedInController switchLoggedInController;

    private final JButton back;
    private final JButton confirm;

    private SellAssetController sellAssetController;

    // variables for functionality
    private final JComboBox<String> portfolioSelector;
    private final JComboBox<String> stockSelector;

    private final JLabel stockPriceLabel;
    private final JTextField quantityField;
    private final JLabel totalPriceLabel;

    private double currentStockPrice;
    private String userName;

    public SellAssetView(final SellAssetViewModel sellAssetViewModel) {
        this.sellAssetViewModel = sellAssetViewModel;
        this.sellAssetViewModel.addPropertyChangeListener(this);

        final JPanel portfolioPanel = new JPanel();
        portfolioPanel.add(new JLabel("Select portfolio:"));
        portfolioSelector = new JComboBox<>();
        portfolioSelector.setPrototypeDisplayValue("Select Portfolio...");
        portfolioPanel.add(portfolioSelector);
        this.add(portfolioPanel);

        final JPanel stockPanel = new JPanel();
        stockPanel.add(new JLabel("Select stock:"));
        stockSelector = new JComboBox<>();
        stockSelector.setPrototypeDisplayValue("Select stock...");
        stockPanel.add(stockSelector);
        this.add(stockPanel);

        final JPanel stockPricePanel = new JPanel();
        stockPricePanel.add(new JLabel("Stock price:"));
        stockPriceLabel = new JLabel("—");
        stockPricePanel.add(stockPriceLabel);
        this.add(stockPricePanel);

        final JPanel quantityPanel = new JPanel();
        quantityPanel.add(new JLabel("Quantity to sell:"));
        quantityField = new JTextField(10);
        quantityPanel.add(quantityField);
        this.add(quantityPanel);

        final JPanel totalPricePanel = new JPanel();
        totalPricePanel.add(new JLabel("Total price:"));
        totalPriceLabel = new JLabel("—");
        totalPricePanel.add(totalPriceLabel);
        this.add(totalPricePanel);

        final JPanel buttons = new JPanel();
        confirm = new JButton("confirm");
        back = new JButton("Back");
        buttons.add(confirm);
        buttons.add(back);
        this.add(buttons);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        portfolioSelector.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        // Clear messages when changing portfolio
                        final SellAssetState state = sellAssetViewModel.getState();
                        state.setMessage(null);
                        state.setErrorMessage(null);
                        state.setPriceError(null);
                        state.setCurrentPrice(0.0);
                        stockPriceLabel.setText("—");

                        // Populate stock selector based on selected portfolio
                        final String selectedPortfolio = (String) portfolioSelector.getSelectedItem();
                        if (selectedPortfolio != null) {
                            final String[] stocks = state.getStocksOfPortfolio(selectedPortfolio);
                            if (stocks != null) {
                                stockSelector.setModel(new DefaultComboBoxModel<>(stocks));
                                stockSelector.setSelectedItem(null);
                            }
                        }

                        totalPriceLabel.setText("—");
                        sellAssetViewModel.setState(state);
                    }
                }
        );

        stockSelector.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final String stockName = (String) stockSelector.getSelectedItem();

                        // CLEAR previous messages when selecting a new stock
                        final SellAssetState state = sellAssetViewModel.getState();
                        state.setMessage(null);
                        state.setErrorMessage(null);
                        state.setPriceError(null);
                        state.setCurrentPrice(0.0);
                        sellAssetViewModel.setState(state);

                        if (stockName != null && !stockName.isEmpty() && sellAssetController != null) {
                            sellAssetController.fetchPrice(stockName);
                        }
                    }
                }
        );

        confirm.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final String portfolioName = (String) portfolioSelector.getSelectedItem();
                    final String stockName = (String) stockSelector.getSelectedItem();
                    final double quantity = Double.parseDouble(quantityField.getText());
                    sellAssetController.execute(userName, portfolioName, stockName, quantity);
                }
            }
        );

        back.addActionListener(
            evt -> {
                if (evt.getSource().equals(back)) {
                    switchLoggedInController.switchToLoggedInView();
                }
            }
        );

        // listener
        quantityField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(final javax.swing.event.DocumentEvent documentEvent) {
                updateTotal();
            }

            public void removeUpdate(final javax.swing.event.DocumentEvent documentEvent) {
                updateTotal();
            }

            public void insertUpdate(final javax.swing.event.DocumentEvent documentEvent) {
                updateTotal();
            }
        });
        currentStockPrice = 0.0;
    }

    private void updateTotal() {
        try {
            final double qty = Double.parseDouble(quantityField.getText());
            final double total = qty * sellAssetViewModel.getState().getCurrentPrice();
            totalPriceLabel.setText("$" + String.format("%.2f", total));
        }
        catch (final Exception ex) {
            totalPriceLabel.setText("—");
        }
    }

    public void actionPerformed(final ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final SellAssetState state = sellAssetViewModel.getState();
        this.currentStockPrice = state.getCurrentPrice();

        // Update portfolio list if present
        if (state.getUsername() != null) {
            userName = state.getUsername();
        }

        if (state.getPortfolios() != null) {
            // Save current selection
            final String currentSelection = (String) portfolioSelector.getSelectedItem();

            // Only update if the list actually changed
            boolean needsUpdate = false;
            if (portfolioSelector.getItemCount() != state.getPortfolios().length) {
                needsUpdate = true;
            } else {
                for (int i = 0; i < state.getPortfolios().length; i++) {
                    if (!state.getPortfolios()[i].equals(portfolioSelector.getItemAt(i))) {
                        needsUpdate = true;
                        break;
                    }
                }
            }

            if (needsUpdate) {
                portfolioSelector.setModel(new DefaultComboBoxModel<>(state.getPortfolios()));

                // Restore previous selection if it still exists
                if (currentSelection != null) {
                    for (int i = 0; i < portfolioSelector.getItemCount(); i++) {
                        if (portfolioSelector.getItemAt(i).equals(currentSelection)) {
                            portfolioSelector.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        }

        // Handle success message
        if (state.getMessage() != null && !state.getMessage().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getMessage(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Clear the message immediately after showing
            quantityField.setText("");
            stockPriceLabel.setText("—");
            totalPriceLabel.setText("—");
            state.setMessage(null);
            sellAssetViewModel.setState(state);
            return;
        }

        // Handle error message
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getErrorMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            // Clear after showing
            state.setErrorMessage(null);
            sellAssetViewModel.setState(state);
            return;
        }

        // Handle price error
        if (state.getPriceError() != null && !state.getPriceError().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getPriceError(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            stockPriceLabel.setText("—");
            totalPriceLabel.setText("—");

            // Clear after showing
            state.setPriceError(null);
            sellAssetViewModel.setState(state);
            return;
        }

        // Update price label only if valid price
        if (state.getCurrentPrice() > 0) {
            stockPriceLabel.setText("$" + String.format("%.2f", state.getCurrentPrice()));

            // Recompute total if quantity entered
            try {
                final double qty = Double.parseDouble(quantityField.getText());
                final double total = qty * state.getCurrentPrice();
                totalPriceLabel.setText("$" + String.format("%.2f", total));
            }
            catch (final Exception ignored) {
                totalPriceLabel.setText("—");
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setSellAssetController(final SellAssetController sellAssetController) {
        this.sellAssetController = sellAssetController;
    }

    public void setSwitchLoggedInController(final SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }
}
