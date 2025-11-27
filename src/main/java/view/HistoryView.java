package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

import interfaceadapter.SwitchLoggedInController;
import interfaceadapter.history.HistoryState;
import interfaceadapter.history.HistoryViewModel;
import interfaceadapter.history.TransactionHistoryController;

public class HistoryView extends JPanel implements ActionListener, PropertyChangeListener {

    private final HistoryViewModel historyViewModel;

    private SwitchLoggedInController switchLoggedInController;
    private TransactionHistoryController transactionHistoryController;

    private final JTextField portfolioField;
    private final JTextField assetField;
    private final JSpinner fromDateSpinner;
    private final JSpinner toDateSpinner;
    private final JLabel messageLabel;

    // table
    private final DefaultTableModel tableModel;

    public HistoryView(final HistoryViewModel historyViewModel) {
        this.historyViewModel = historyViewModel;
        this.historyViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        // ======== TOP BAR (back button) ========
        final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // filters
        final JButton back = new JButton("Back");
        topPanel.add(back);
        add(topPanel, BorderLayout.NORTH);

        // ======== FILTER PANEL ========
        final JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        portfolioField = new JTextField(10);
        assetField = new JTextField(10);

        // date spinners (yyyy-MM-dd)
        final SpinnerDateModel fromModel = new SpinnerDateModel();
        final SpinnerDateModel toModel = new SpinnerDateModel();
        fromDateSpinner = new JSpinner(fromModel);
        toDateSpinner = new JSpinner(toModel);
        final JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd");
        final JSpinner.DateEditor toEditor = new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd");
        fromDateSpinner.setEditor(fromEditor);
        toDateSpinner.setEditor(toEditor);

        final JButton loadButton = new JButton("Load");
        final JButton clearButton = new JButton("Clear");

        filterPanel.add(new JLabel("Portfolio ID:"));
        filterPanel.add(portfolioField);
        filterPanel.add(new JLabel("Asset:"));
        filterPanel.add(assetField);
        filterPanel.add(new JLabel("From:"));
        filterPanel.add(fromDateSpinner);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(toDateSpinner);
        filterPanel.add(loadButton);
        filterPanel.add(clearButton);

        add(filterPanel, BorderLayout.CENTER);

        // ======== TABLE + MESSAGE (BOTTOM) ========
        tableModel = new DefaultTableModel(
            new Object[] {"ID", "Date/Time", "Asset", "Type", "Qty", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };
        final JTable table = new JTable(tableModel);
        final JScrollPane scrollPane = new JScrollPane(table);

        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        messageLabel = new JLabel(" ");
        bottomPanel.add(messageLabel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // ======== LISTENERS ========

        back.addActionListener(evt -> {
            if (switchLoggedInController != null) {
                switchLoggedInController.switchToLoggedInView();
            }
        });

        loadButton.addActionListener((final ActionEvent e) -> {
            final String portfolioId = portfolioField.getText().trim();
            final String asset = assetField.getText().trim();

            System.out.println("[DEBUG] Load clicked");
            System.out.println("[DEBUG] portfolioId = '" + portfolioId + "'");
            System.out.println("[DEBUG] asset = '" + asset + "'");

            if (portfolioId.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a portfolio ID.",
                    "Input error",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            final String fromDateStr = ((JSpinner.DateEditor) fromDateSpinner.getEditor())
                .getFormat().format(fromDateSpinner.getValue());
            final String toDateStr = ((JSpinner.DateEditor) toDateSpinner.getEditor())
                .getFormat().format(toDateSpinner.getValue());

            if (transactionHistoryController != null) {
                transactionHistoryController.loadHistory(
                    portfolioId,
                    asset.isEmpty() ? null : asset,
                    fromDateStr,
                    toDateStr
                );
            }
            else {
                // temporary debug if controller not wired
                JOptionPane.showMessageDialog(this,
                    "TransactionHistoryController is null – check AppBuilder wiring.");
            }
        });

        clearButton.addActionListener((final ActionEvent e) -> {
            portfolioField.setText("");
            assetField.setText("");
            messageLabel.setText("Cleared.");
            tableModel.setRowCount(0);
        });
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        // HistoryViewModel fired "state" change
        final HistoryState state = historyViewModel.getState();
        if (state == null) {
            return;
        }

        System.out.println("[View] propertyChange fired for '"
            + evt.getPropertyName() + "'");
        System.out.println("[View] rows to display = " + state.getRows().size());
        System.out.println("[View] message        = " + state.getMessage());

        // update message
        messageLabel.setText(state.getMessage());

        // update table rows
        tableModel.setRowCount(0);
        for (final HistoryState.Row r : state.getRows()) {
            tableModel.addRow(new Object[] {
                r.id,
                r.dateTime,
                r.asset,
                r.type,
                r.quantity,
                r.totalValue
            });
        }
    }


    public String getViewName() {
        final String viewName = "history";
        return viewName;
    }

    public void setSwitchLoggedInController(final SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }

    public void setTransactionHistoryController(final TransactionHistoryController controller) {
        this.transactionHistoryController = controller;
    }
}

