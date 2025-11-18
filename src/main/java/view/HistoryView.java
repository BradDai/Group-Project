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
