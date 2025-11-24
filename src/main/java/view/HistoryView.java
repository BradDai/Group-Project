/*
package view;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.history.HistoryState;
import interface_adapter.history.HistoryViewModel;
import interface_adapter.transaction_history.TransactionHistoryController;

import javax.swing.*;
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

    // UI components
    private final JButton back;
    private final JTextField portfolioField;
    private final JTextField assetField;
    private final JButton loadButton;
    private final JButton clearButton;
    private final JLabel messageLabel;

    public HistoryView(HistoryViewModel historyViewModel) {
        this.historyViewModel = historyViewModel;
        this.historyViewModel.addPropertyChangeListener(this);

        // ----- layout -----
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Top: back button
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        back = new JButton("Back");
        topButtons.add(back);
        this.add(topButtons);

        // Middle: input fields + load/clear
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portfolioField = new JTextField(10);
        assetField = new JTextField(10);
        loadButton = new JButton("Load");
        clearButton = new JButton("Clear");

        inputPanel.add(new JLabel("Portfolio ID:"));
        inputPanel.add(portfolioField);
        inputPanel.add(new JLabel("Asset:"));
        inputPanel.add(assetField);
        inputPanel.add(loadButton);
        inputPanel.add(clearButton);

        this.add(inputPanel);

        // Bottom: message label
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messageLabel = new JLabel(" ");
        bottomPanel.add(messageLabel);
        this.add(bottomPanel);

        // ----- listeners -----//

        // Back to logged-in view
        back.addActionListener(evt -> {
            if (evt.getSource().equals(back)) {
                if (switchLoggedInController != null) {
                    switchLoggedInController.switchToLoggedInView();
                }
            }
        });

        // Load
        loadButton.addActionListener((ActionEvent e) -> {
            String portfolioId = portfolioField.getText().trim();
            String asset = assetField.getText().trim();

            if (portfolioId.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a portfolio ID.",
                        "Input error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (transactionHistoryController != null) {
                transactionHistoryController.loadHistory(
                        portfolioId,
                        asset.isEmpty() ? null : asset
                );
            }
            // presenter will update HistoryViewModel, and propertyChange()
            // below will refresh the message label.
        });

        // Clear
        clearButton.addActionListener((ActionEvent e) -> {
            portfolioField.setText("");
            assetField.setText("");
            messageLabel.setText("Cleared.");
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Whenever HistoryViewModel changes, update UI
        HistoryState state = historyViewModel.getState();
        if (state != null) {
            messageLabel.setText(state.getMessage());
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
*/

package view;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.history.HistoryState;
import interface_adapter.history.HistoryViewModel;
import interface_adapter.history.TransactionHistoryController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HistoryView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "history";
    private final HistoryViewModel historyViewModel;
    private SwitchLoggedInController switchLoggedInController;
    private TransactionHistoryController transactionHistoryController;

    // UI components
    private final JButton back;
    private final JTextField portfolioField;
    private final JTextField assetField;
    private final JSpinner startDateSpinner;
    private final JSpinner endDateSpinner;
    private final JButton loadButton;
    private final JButton clearButton;
    private final JLabel messageLabel;

    public HistoryView(HistoryViewModel historyViewModel) {
        this.historyViewModel = historyViewModel;
        this.historyViewModel.addPropertyChangeListener(this);

        // ----- layout -----
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Top: back button
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        back = new JButton("Back");
        topButtons.add(back);
        this.add(topButtons);

        // Middle: input fields + date filter + load
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        portfolioField = new JTextField(10);
        assetField = new JTextField(10);
        loadButton = new JButton("Load");
        clearButton = new JButton("Clear");

        // Date spinners (scrollable)
        Date today = new Date();
        SpinnerDateModel startModel =
                new SpinnerDateModel(today, null, null, Calendar.DAY_OF_MONTH);
        SpinnerDateModel endModel =
                new SpinnerDateModel(today, null, null, Calendar.DAY_OF_MONTH);

        startDateSpinner = new JSpinner(startModel);
        endDateSpinner = new JSpinner(endModel);

        // show as yyyy-MM-dd
        startDateSpinner.setEditor(
                new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        endDateSpinner.setEditor(
                new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));

        inputPanel.add(new JLabel("Portfolio ID:"));
        inputPanel.add(portfolioField);

        inputPanel.add(new JLabel("Asset:"));
        inputPanel.add(assetField);

        inputPanel.add(new JLabel("From:"));
        inputPanel.add(startDateSpinner);

        inputPanel.add(new JLabel("To:"));
        inputPanel.add(endDateSpinner);

        inputPanel.add(loadButton);
        inputPanel.add(clearButton);

        this.add(inputPanel);

        // Bottom: message label
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messageLabel = new JLabel(" ");
        bottomPanel.add(messageLabel);
        this.add(bottomPanel);

        // ----- listeners ----- //

        // Back to logged-in view
        back.addActionListener(evt -> {
            if (evt.getSource().equals(back)) {
                if (switchLoggedInController != null) {
                    switchLoggedInController.switchToLoggedInView();
                }
            }
        });

        // Load
        loadButton.addActionListener((ActionEvent e) -> {
            String portfolioId = portfolioField.getText().trim();
            String asset = assetField.getText().trim();

            if (portfolioId.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a portfolio ID.",
                        "Input error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // format dates from spinners
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = fmt.format((Date) startDateSpinner.getValue());
            String endDate = fmt.format((Date) endDateSpinner.getValue());

            if (transactionHistoryController != null) {
                transactionHistoryController.loadHistory(
                        portfolioId,
                        asset.isEmpty() ? null : asset,
                        startDate,
                        endDate
                );
            }
            // presenter will update HistoryViewModel, and propertyChange()
            // below will refresh the message label / table.
        });

        // Clear
        clearButton.addActionListener((ActionEvent e) -> {
            portfolioField.setText("");
            assetField.setText("");
            messageLabel.setText("Cleared.");
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Whenever HistoryViewModel changes, update UI
        HistoryState state = historyViewModel.getState();
        if (state != null) {
            messageLabel.setText(state.getMessage());
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
