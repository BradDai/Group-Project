
package view;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.history.HistoryState;
import interface_adapter.history.HistoryViewModel;
import interface_adapter.history.TransactionHistoryController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class HistoryView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "history";
    private final HistoryViewModel historyViewModel;

    private SwitchLoggedInController switchLoggedInController;
    private TransactionHistoryController transactionHistoryController;

    // filters
    private final JButton back;
    private final JTextField portfolioField;
    private final JTextField assetField;
    private final JSpinner fromDateSpinner;
    private final JSpinner toDateSpinner;
    private final JButton loadButton;
    private final JButton clearButton;
    private final JLabel messageLabel;

    // table
    private final DefaultTableModel tableModel;
    private final JTable table;

    public HistoryView(HistoryViewModel historyViewModel) {
        this.historyViewModel = historyViewModel;
        this.historyViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        // ======== TOP BAR (back button) ========
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        back = new JButton("Back");
        topPanel.add(back);
        add(topPanel, BorderLayout.NORTH);

        // ======== FILTER PANEL ========
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        portfolioField = new JTextField(10);
        assetField = new JTextField(10);

        // date spinners (yyyy-MM-dd)
        SpinnerDateModel fromModel = new SpinnerDateModel();
        SpinnerDateModel toModel = new SpinnerDateModel();
        fromDateSpinner = new JSpinner(fromModel);
        toDateSpinner = new JSpinner(toModel);
        JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor toEditor = new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd");
        fromDateSpinner.setEditor(fromEditor);
        toDateSpinner.setEditor(toEditor);

        loadButton = new JButton("Load");
        clearButton = new JButton("Clear");

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
                new Object[]{"ID", "Date/Time", "Asset", "Type", "Qty", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel();
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

        loadButton.addActionListener((ActionEvent e) -> {
            String portfolioId = portfolioField.getText().trim();
            String asset = assetField.getText().trim();

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

            String fromDateStr = ((JSpinner.DateEditor) fromDateSpinner.getEditor())
                    .getFormat().format(fromDateSpinner.getValue());
            String toDateStr = ((JSpinner.DateEditor) toDateSpinner.getEditor())
                    .getFormat().format(toDateSpinner.getValue());

            if (transactionHistoryController != null) {
                transactionHistoryController.loadHistory(
                        portfolioId,
                        asset.isEmpty() ? null : asset,
                        fromDateStr,
                        toDateStr
                );
            } else {
                // temporary debug if controller not wired
                JOptionPane.showMessageDialog(this,
                        "TransactionHistoryController is null – check AppBuilder wiring.");
            }
        });

        clearButton.addActionListener((ActionEvent e) -> {
            portfolioField.setText("");
            assetField.setText("");
            messageLabel.setText("Cleared.");
            tableModel.setRowCount(0);
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // HistoryViewModel fired "state" change
        HistoryState state = historyViewModel.getState();
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
        for (HistoryState.Row r : state.getRows()) {
            tableModel.addRow(new Object[]{
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
        return viewName;
    }

    public void setSwitchLoggedInController(SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }

    public void setTransactionHistoryController(TransactionHistoryController controller) {
        this.transactionHistoryController = controller;
    }
}

