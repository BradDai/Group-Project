package view.transfercomponents;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dataaccess.Constants;

/**
 * Top panel containing From Portfolio, To Portfolio, and Transfer Type dropdowns.
 */
public class TopPanel extends JPanel {

    private final JComboBox<String> fromPortfolioDropdown;
    private final JComboBox<String> toPortfolioDropdown;
    private final JComboBox<String> transferTypeDropdown;

    public TopPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        fromPortfolioDropdown = new JComboBox<>();
        toPortfolioDropdown = new JComboBox<>();
        transferTypeDropdown = new JComboBox<>(new String[] {
            Constants.TRANSFER_STOCK,
            Constants.TRANSFER_CURRENCY,
        });

        final JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fromPanel.add(new JLabel("Transfer from Portfolio:"));
        fromPanel.add(fromPortfolioDropdown);
        this.add(fromPanel);

        final JPanel toPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toPanel.add(new JLabel("Transfer to Portfolio:"));
        toPanel.add(toPortfolioDropdown);
        this.add(toPanel);

        final JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.add(new JLabel("Type of Transfer:"));
        typePanel.add(transferTypeDropdown);
        this.add(typePanel);
    }

    /**
     * Adds a shared listener to all dropdowns to trigger updates.
     *
     * @param listener The action listener
     */
    public void addActionListener(final ActionListener listener) {
        fromPortfolioDropdown.addActionListener(listener);
        toPortfolioDropdown.addActionListener(listener);
        transferTypeDropdown.addActionListener(listener);
    }

    /**
     * Updates the portfolio lists in the dropdowns.
     * Preserves the current selection if it still exists in the new list.
     *
     * @param portfolios Array of portfolio names
     */
    public void updatePortfolios(final String[] portfolios) {
        final Object currentFrom = fromPortfolioDropdown.getSelectedItem();
        final Object currentTo = toPortfolioDropdown.getSelectedItem();

        fromPortfolioDropdown.removeAllItems();
        toPortfolioDropdown.removeAllItems();

        if (portfolios != null) {
            for (final String p : portfolios) {
                fromPortfolioDropdown.addItem(p);
                toPortfolioDropdown.addItem(p);
            }
        }

        // Restore or set default for 'From' dropdown
        if (currentFrom != null) {
            fromPortfolioDropdown.setSelectedItem(currentFrom);
        }
        if (fromPortfolioDropdown.getSelectedIndex()
            == -1 && fromPortfolioDropdown.getItemCount() > Constants.INDEX_FIRST) {
            fromPortfolioDropdown.setSelectedIndex(Constants.INDEX_FIRST);
        }

        // Restore or set default for 'To' dropdown
        if (currentTo != null) {
            toPortfolioDropdown.setSelectedItem(currentTo);
        }
        if (toPortfolioDropdown.getSelectedIndex() == -1) {
            if (toPortfolioDropdown.getItemCount() > Constants.INDEX_SECOND) {
                toPortfolioDropdown.setSelectedIndex(Constants.INDEX_SECOND);
            }
            else if (toPortfolioDropdown.getItemCount() > 0) {
                toPortfolioDropdown.setSelectedIndex(0);
            }
        }
    }

    public String getFromPortfolio() {
        return (String) fromPortfolioDropdown.getSelectedItem();
    }

    public String getToPortfolio() {
        return (String) toPortfolioDropdown.getSelectedItem();
    }

    public String getTransferType() {
        return (String) transferTypeDropdown.getSelectedItem();
    }
}
