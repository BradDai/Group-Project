package view.transfer_components;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import data_access.Constants;

/**
 * Panel containing the Confirm and Cancel buttons for the Transfer view.
 */
public class TransferButtonPanel extends JPanel {
    private final JButton confirmButton;
    private final JButton cancelButton;

    public TransferButtonPanel() {
        confirmButton = new JButton(Constants.BUTTON_CONFIRM);
        cancelButton = new JButton(Constants.BUTTON_CANCEL);
        this.add(confirmButton);
        this.add(cancelButton);
    }

    /**
     * Adds a listener to the confirm button.
     *
     * @param listener the listener to add
     */
    public void addConfirmListener(final ActionListener listener) {
        confirmButton.addActionListener(listener);
    }

    /**
     * Adds a listener to the cancel button.
     *
     * @param listener the listener to add
     */
    public void addCancelListener(final ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    /**
     * Sets the enabled state of the confirm button.
     *
     * @param enabled true to enable, false to disable
     */
    public void setConfirmEnabled(final boolean enabled) {
        confirmButton.setEnabled(enabled);
    }
}
