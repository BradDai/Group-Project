package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

import interfaceadapter.SwitchLoggedInController;
import interfaceadapter.history.HistoryState;
import interfaceadapter.history.HistoryViewModel;
import interfaceadapter.history.TransactionHistoryController;

public class HistoryView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "history";

    private final HistoryViewModel historyViewModel;

    private SwitchLoggedInController switchLoggedInController;
    private TransactionHistoryController transactionHistoryController;

    private final JComboBox<String> portfolioComboBox;
    private final javax.swing.JTextField assetField;
    private final JSpinner fromDateSpinner;
    private final JSpinner toDateSpinner;
    private final JLabel messageLabel;

    private final DefaultTableModel tableModel;

    public HistoryView(final HistoryViewModel historyViewModel) {
        this.historyViewModel = historyViewModel;
        this.historyViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("Back");
        topPanel.add(back);
        add(topPanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("Portfolio ID:"));
        portfolioComboBox = new JComboBox<>();
        portfolioComboBox.addItem("");
        filterPanel.add(portfolioComboBox);

        filterPanel.add(new JLabel("Asset:"));
        assetField = new javax.swing.JTextField(10);
        filterPanel.add(assetField);

        SpinnerDateModel fromModel = new SpinnerDateModel();
        SpinnerDateModel toModel = new SpinnerDateModel();
        fromDateSpinner = new JSpinner(fromModel);
        toDateSpinner = new JSpinner(toModel);
        JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor toEditor = new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd");
        fromDateSpinner.setEditor(fromEditor);
        toDateSpinner.setEditor(toEditor);

        filterPanel.add(new JLabel("From:"));
        filterPanel.add(fromDateSpinner);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(toDateSpinner);

        JButton loadButton = new JButton("Load");
        JButton clearButton = new JButton("Clear");
        filterPanel.add(loadButton);
        filterPanel.add(clearButton);

        add(filterPanel, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Date/Time", "Asset", "Type", "Qty", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageLabel = new JLabel(" ");
        bottomPanel.add(messageLabel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        back.addActionListener(evt -> {
            if (switchLoggedInController != null) {
                switchLoggedInController.switchToLoggedInView();
            }
        });

        loadButton.addActionListener(e -> onLoadClicked());
        clearButton.addActionListener(e -> onClearClicked());

        portfolioComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                if (transactionHistoryController != null) {
                    transactionHistoryController.loadPortfolioOptions();
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) { }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) { }
        });
    }

    private void onLoadClicked() {
        String portfolioId = (String) portfolioComboBox.getSelectedItem();
        String asset = assetField.getText().trim();

        if (portfolioId == null || portfolioId.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a portfolio.",
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
            JOptionPane.showMessageDialog(
                    this,
                    "TransactionHistoryController is null – check wiring.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onClearClicked() {
        portfolioComboBox.setSelectedItem("");
        assetField.setText("");
        messageLabel.setText("Cleared.");
        tableModel.setRowCount(0);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }

        HistoryState state = historyViewModel.getState();
        if (state == null) {
            return;
        }

        messageLabel.setText(state.getMessage() == null ? "" : state.getMessage());

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

        List<String> options = state.getPortfolioOptions();
        if (options != null) {
            Object previous = portfolioComboBox.getSelectedItem();

            portfolioComboBox.removeAllItems();
            portfolioComboBox.addItem("");
            for (String p : options) {
                portfolioComboBox.addItem(p);
            }

            if (previous != null && !previous.toString().isBlank()) {
                portfolioComboBox.setSelectedItem(previous);
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        // no-op
    }

    public String getViewName() {
        return viewName;
    }

    public void setSwitchLoggedInController(final SwitchLoggedInController controller) {
        this.switchLoggedInController = controller;
    }

    public void setTransactionHistoryController(final TransactionHistoryController controller) {
        this.transactionHistoryController = controller;
    }
}







