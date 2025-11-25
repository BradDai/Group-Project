package view.transfer_components;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data_access.Constants;

public class BalancePanel extends JPanel {
    private final JLabel fromBalanceLabel;
    private final JLabel toBalanceLabel;

    public BalancePanel() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        final Box balanceBox = Box.createVerticalBox();

        fromBalanceLabel = new JLabel(Constants.LABEL_SENDER_BALANCE);
        toBalanceLabel = new JLabel(Constants.LABEL_RECEIVER_BALANCE);

        fromBalanceLabel.setForeground(Color.DARK_GRAY);
        toBalanceLabel.setForeground(Color.DARK_GRAY);

        balanceBox.add(fromBalanceLabel);
        balanceBox.add(Box.createVerticalStrut(Constants.VERTICAL_SPACING_SMALL));
        balanceBox.add(toBalanceLabel);

        this.add(balanceBox);
    }

    /**
     * Updates the text of the sender and receiver balance labels.
     *
     * @param typeLabel   the label indicating the type of balance (e.g., "Quantity", "Balance")
     * @param fromBalance the balance of the sender to display
     * @param toBalance   the balance of the receiver to display
     */
    public void updateBalances(final String typeLabel, final String fromBalance, final String toBalance) {
        fromBalanceLabel.setText(Constants.LABEL_SENDER + typeLabel + ": " + fromBalance);
        toBalanceLabel.setText(Constants.LABEL_RECEIVER + typeLabel + ": " + toBalance);
    }
}
