package view.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import data_access.Constants;

public class CurrencyPanel extends JPanel {

    private final JComboBox<String> currencyDropdown;
    private final JTextField amountField;
    private final JLabel errorLabel;

    public CurrencyPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createTitledBorder(Constants.LABEL_CURRENCY_PANEL));

        currencyDropdown = new JComboBox<>();
        amountField = new JTextField(Constants.TEXTFIELD_COLUMNS);

        final JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.add(new JLabel(Constants.LABEL_CURRENCY));
        typePanel.add(currencyDropdown);
        this.add(typePanel);

        final JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.add(new JLabel(Constants.LABEL_AMOUNT));
        amountPanel.add(amountField);
        this.add(amountPanel);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(errorLabel);

        this.setVisible(false);
    }

    /**
     * Sets the available currencies in the dropdown.
     *
     * @param currencies an array of currency codes to populate the dropdown
     */
    public void setAvailableCurrencies(final String[] currencies) {
        final Object currentSelection = currencyDropdown.getSelectedItem();
        currencyDropdown.removeAllItems();
        if (currencies != null) {
            for (final String c : currencies) {
                currencyDropdown.addItem(c);
            }
        }
        if (currencyDropdown.getItemCount() == 0) {
            currencyDropdown.addItem(Constants.DEFAULT_CURRENCY);
        }
        if (currentSelection != null) {
            for (int i = 0; i < currencyDropdown.getItemCount(); i++) {
                if (currencyDropdown.getItemAt(i).equals(currentSelection)) {
                    currencyDropdown.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public String getSelectedCurrency() {
        return (String) currencyDropdown.getSelectedItem();
    }

    /**
     * Returns the current amount entered.
     *
     * @return the amount in the field
     * @throws NumberFormatException if the text is not a valid number
     */
    public double getAmount() throws NumberFormatException {
        return Double.parseDouble(amountField.getText().trim());
    }

    /**
     * Sets the text in the amount field.
     *
     * @param text the string value to set in the amount field
     */
    public void setAmountText(final String text) {
        if (!amountField.getText().equals(text)) {
            amountField.setText(text);
        }
    }

    /**
     * Displays an error message below the amount field.
     *
     * @param text the error message to display
     */
    public void showError(final String text) {
        errorLabel.setText(text);
    }

    /**
     * Adds an ActionListener to the currency dropdown.
     *
     * @param listener the ActionListener to be notified on selection changes
     */
    public void addActionListener(final ActionListener listener) {
        currencyDropdown.addActionListener(listener);
    }

    /**
     * Adds a listener to run whenever the amount field is updated.
     *
     * @param listener a Runnable to execute on text changes
     */
    public void addAmountChangeListener(final Runnable listener) {
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                listener.run();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                listener.run();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                listener.run();
            }
        });
    }

    /**
     * Validates the amount in the amount field and updates the confirm button.
     *
     * @param buttonPanel the TransferButtonPanel that contains the confirm button
     */
    public void validateAmount(final TransferButtonPanel buttonPanel) {
        try {
            final String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                showError("");
                buttonPanel.setConfirmEnabled(true);
            }
            else {
                final double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    showError(Constants.ERROR_AMOUNT_POSITIVE);
                    buttonPanel.setConfirmEnabled(false);
                }
                else {
                    showError("");
                    buttonPanel.setConfirmEnabled(true);
                }
            }
        }
        catch (final NumberFormatException numberFormatException) {
            showError(Constants.ERROR_AMOUNT_FORMAT);
            buttonPanel.setConfirmEnabled(false);
        }
    }
}
