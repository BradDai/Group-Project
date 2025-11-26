package view.transfercomponents;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import dataaccess.Constants;

public class StockPanel extends JPanel {
    private final JComboBox<String> stockSymbolDropdown;
    private final JSpinner stockAmountSpinner;

    public StockPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createTitledBorder(Constants.LABEL_STOCK_PANEL));

        stockSymbolDropdown = new JComboBox<>();
        stockAmountSpinner = new JSpinner(new SpinnerNumberModel(
            Constants.SPINNER_MIN, Constants.SPINNER_MIN,
            Constants.SPINNER_MAX, Constants.SPINNER_STEP));

        final JPanel symbolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        symbolPanel.add(new JLabel(Constants.LABEL_SYMBOL));
        symbolPanel.add(stockSymbolDropdown);
        this.add(symbolPanel);

        final JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.add(new JLabel(Constants.LABEL_QUANTITY));
        amountPanel.add(stockAmountSpinner);
        this.add(amountPanel);
    }

    /**
     * Populates the stock dropdown with the provided stock symbols.
     * Preserves the current selection if it still exists in the new list.
     *
     * @param stocks an array of stock symbols to populate the dropdown
     */
    public void setAvailableStocks(final String[] stocks) {
        final Object currentSelection = stockSymbolDropdown.getSelectedItem();
        stockSymbolDropdown.removeAllItems();

        if (stocks != null) {
            for (final String s : stocks) {
                stockSymbolDropdown.addItem(s);
            }
        }

        // Restore previous selection if possible
        if (currentSelection != null) {
            stockSymbolDropdown.setSelectedItem(currentSelection);
        }

        // If nothing selected (or restored item invalid), select first
        if (stockSymbolDropdown.getSelectedIndex() == -1 && stockSymbolDropdown.getItemCount() > 0) {
            stockSymbolDropdown.setSelectedIndex(0);
        }
    }

    /**
     * Returns the currently selected stock symbol.
     *
     * @return the selected stock symbol, or null if none is selected
     */
    public String getSelectedStock() {
        return (String) stockSymbolDropdown.getSelectedItem();
    }

    /**
     * Returns the amount specified in the spinner.
     *
     * @return the integer value from the stock amount spinner
     */
    public int getAmount() {
        return (Integer) stockAmountSpinner.getValue();
    }

    /**
     * Adds an ActionListener to the stock dropdown to listen for selection changes.
     *
     * @param listener the ActionListener to be notified on selection changes
     */
    public void addActionListener(final ActionListener listener) {
        stockSymbolDropdown.addActionListener(listener);
    }
}
