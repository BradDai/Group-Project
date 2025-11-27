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
    private static final String SPACE_PLACEHOLDER = "";
    private static final String DOLLAR_PLACEHOLDER = "$";
    private static final String DECIMAL_PLACEHOLDER = "%.2f";

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

    public SellAssetView(final SellAssetViewModel sellAssetViewModel) {
        final int ten = 10;

        this.sellAssetViewModel = sellAssetViewModel;
        this.sellAssetViewModel.addPropertyChangeListener(this);

        portfolioSelector = new JComboBox<>();
        stockSelector = new JComboBox<>();
        stockPriceLabel = new JLabel(SPACE_PLACEHOLDER);
        quantityField = new JTextField(ten);
        totalPriceLabel = new JLabel(SPACE_PLACEHOLDER);
        confirm = new JButton("confirm");
        back = new JButton("Back");

        buildUi();
        addListeners();
    }

    private void buildUi() {
        addPortfolioSection();
        addStockSection();
        addPriceSection();
        addQuantitySection();
        addButtonsSection();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    private void addPortfolioSection() {
        final JPanel portfolioPanel = new JPanel();
        portfolioPanel.add(new JLabel("Select portfolio:"));
        portfolioSelector.setPrototypeDisplayValue("                ");
        portfolioSelector.setModel(new DefaultComboBoxModel<>(new String[]{""}));
        portfolioSelector.setSelectedIndex(0);
        portfolioPanel.add(portfolioSelector);
        this.add(portfolioPanel);
    }

    private void addStockSection() {
        final JPanel stockPanel = new JPanel();
        stockPanel.add(new JLabel("Select stock:"));
        stockSelector.setPrototypeDisplayValue("       ");
        stockSelector.setModel(new DefaultComboBoxModel<>(new String[]{""}));
        stockSelector.setSelectedIndex(0);
        stockPanel.add(stockSelector);
        this.add(stockPanel);
    }

    private void addPriceSection() {
        final JPanel stockPricePanel = new JPanel();
        stockPricePanel.add(new JLabel("Stock price:"));
        stockPriceLabel.setText(SPACE_PLACEHOLDER);
        stockPricePanel.add(stockPriceLabel);
        this.add(stockPricePanel);
    }

    private void addQuantitySection() {
        final JPanel quantityPanel = new JPanel();
        quantityPanel.add(new JLabel("Quantity to sell:"));
        quantityPanel.add(quantityField);
        this.add(quantityPanel);
    }

    private void addButtonsSection() {
        final JPanel totalPricePanel = new JPanel();
        totalPricePanel.add(new JLabel("Total price:"));
        totalPriceLabel.setText(SPACE_PLACEHOLDER);
        totalPricePanel.add(totalPriceLabel);
        this.add(totalPricePanel);

        final JPanel buttons = new JPanel();
        buttons.add(confirm);
        buttons.add(back);
        this.add(buttons);
    }

    private void addListeners() {
        portfolioSelector.addActionListener(evt -> handlePortfolioSelection());
        stockSelector.addActionListener(evt -> handleStockSelection());
        confirm.addActionListener(evt -> handleConfirm());
        back.addActionListener(evt -> handleBack(evt));
        quantityField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(final javax.swing.event.DocumentEvent evt) {
                handleQuantityUpdate();
            }

            public void removeUpdate(final javax.swing.event.DocumentEvent evt) {
                handleQuantityUpdate();
            }

            public void insertUpdate(final javax.swing.event.DocumentEvent evt) {
                handleQuantityUpdate();
            }
        });
    }

    private void handlePortfolioSelection() {
        final SellAssetState state = sellAssetViewModel.getState();
        state.setMessage(null);
        state.setErrorMessage(null);
        state.setPriceError(null);
        state.setCurrentPrice(0.0);
        stockPriceLabel.setText(SPACE_PLACEHOLDER);

        final String selectedPortfolio = (String) portfolioSelector.getSelectedItem();
        if (selectedPortfolio != null) {
            final String[] stocks = state.getStocksOfPortfolio(selectedPortfolio);
            if (stocks != null) {
                stockSelector.setModel(new DefaultComboBoxModel<>(stocks));
                stockSelector.setSelectedItem(null);
            }
        }

        totalPriceLabel.setText(SPACE_PLACEHOLDER);
        sellAssetViewModel.setState(state);
    }

    private void handleStockSelection() {
        final String stockName = (String) stockSelector.getSelectedItem();
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

    private void handleConfirm() {
        final String portfolioName = (String) portfolioSelector.getSelectedItem();
        final String stockName = (String) stockSelector.getSelectedItem();
        final double quantity = Double.parseDouble(quantityField.getText());
        sellAssetController.execute(portfolioName, stockName, quantity);
    }

    private void handleBack(final ActionEvent evt) {
        if (evt.getSource().equals(back)) {
            switchLoggedInController.switchToLoggedInView();
        }
    }

    private void handleQuantityUpdate() {
        updateTotal();
    }

    private void updateTotal() {
        try {
            final double qty = Double.parseDouble(quantityField.getText());
            final double total = qty * sellAssetViewModel.getState().getCurrentPrice();
            totalPriceLabel.setText(DOLLAR_PLACEHOLDER + String.format(DECIMAL_PLACEHOLDER, total));
        }
        catch (final NumberFormatException ex) {
            totalPriceLabel.setText(SPACE_PLACEHOLDER);
        }
    }

    private void updatePortfolioList(SellAssetState state) {
        final String currentSelection = (String) portfolioSelector.getSelectedItem();
        boolean needsUpdate = false;
        final int expectedCount = state.getPortfolios().length + 1;

        if (portfolioSelector.getItemCount() != expectedCount) {
            needsUpdate = true;
        }
        else {
            for (int i = 0; i < state.getPortfolios().length; i++) {
                if (!state.getPortfolios()[i].equals(portfolioSelector.getItemAt(i + 1))) {
                    needsUpdate = true;
                    break;
                }
            }
        }

        if (needsUpdate) {
            final String[] portfolios = state.getPortfolios();
            final String[] withBlank = new String[portfolios.length + 1];
            withBlank[0] = "";
            System.arraycopy(portfolios, 0, withBlank, 1, portfolios.length);

            portfolioSelector.setModel(new DefaultComboBoxModel<>(withBlank));

            if (currentSelection != null && !currentSelection.isEmpty()) {
                portfolioSelector.setSelectedItem(currentSelection);
            }
        }
    }

    private boolean handleStateMessage(SellAssetState state) {
        boolean result = false;
        if (state.getMessage() != null && !state.getMessage().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
            quantityField.setText("");
            stockPriceLabel.setText(SPACE_PLACEHOLDER);
            totalPriceLabel.setText(SPACE_PLACEHOLDER);
            state.setMessage(null);
            sellAssetViewModel.setState(state);
            result = true;
        }
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            state.setErrorMessage(null);
            sellAssetViewModel.setState(state);
            result = true;
        }
        if (state.getPriceError() != null && !state.getPriceError().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getPriceError(), "Error", JOptionPane.ERROR_MESSAGE);
            stockPriceLabel.setText(SPACE_PLACEHOLDER);
            totalPriceLabel.setText(SPACE_PLACEHOLDER);
            state.setPriceError(null);
            sellAssetViewModel.setState(state);
            result = true;
        }
        return result;
    }

    private void updatePriceDisplay(SellAssetState state) {
        if (state.getCurrentPrice() > 0) {
            stockPriceLabel.setText(DOLLAR_PLACEHOLDER + String.format(DECIMAL_PLACEHOLDER, state.getCurrentPrice()));
            try {
                final double qty = Double.parseDouble(quantityField.getText());
                final double total = qty * state.getCurrentPrice();
                totalPriceLabel.setText(DOLLAR_PLACEHOLDER + String.format(DECIMAL_PLACEHOLDER, total));
            }
            catch (final NumberFormatException ignored) {
                totalPriceLabel.setText(SPACE_PLACEHOLDER);
            }
        }
    }

    /**
     * Action performed.
     *
     * @param evt the event to be processed
     */
    public void actionPerformed(final ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final SellAssetState state = sellAssetViewModel.getState();

        if (state.getPortfolios() != null) {
            updatePortfolioList(state);
        }

        if (!handleStateMessage(state)) {
            updatePriceDisplay(state);
        }
    }

    /**
     * Return the name of the view.
     *
     * @return name of the view
     */
    public String getViewName() {
        return "sellasset";
    }

    public void setSellAssetController(final SellAssetController sellAssetController) {
        this.sellAssetController = sellAssetController;
    }

    public void setSwitchLoggedInController(final SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }
}
