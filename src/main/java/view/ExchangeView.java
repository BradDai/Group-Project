package view;

import interface_adapter.exchange.ExchangeController;
import interface_adapter.exchange.ExchangeViewModel;
import interface_adapter.SwitchLoggedInController;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExchangeView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "exchange";
    private final ExchangeViewModel exchangeViewModel;
    private ExchangeController exchangeController;
    private SwitchLoggedInController switchLoggedInController;

    private final JButton back;
    private final JComboBox<String> firstCurrency;
    private final JComboBox<String> secondCurrency;
    private final JLabel resultLabel;

    public ExchangeView(ExchangeViewModel exchangeViewModel) {
        this.exchangeViewModel = exchangeViewModel;
        this.exchangeViewModel.addPropertyChangeListener(this);

        final JPanel Buttons = new JPanel();
        back = new JButton("Back");
        Buttons.add(back);
        this.add(Buttons);

        firstCurrency = new JComboBox<>();
        secondCurrency = new JComboBox<>();
        loadCurrencies(firstCurrency, secondCurrency);

        JPanel currencyPanel = new JPanel();
        currencyPanel.add(new JLabel("From:"));
        currencyPanel.add(firstCurrency);
        currencyPanel.add(new JLabel("   To:"));
        currencyPanel.add(secondCurrency);

        JPanel resultPanel = new JPanel();
        resultPanel.add(new JLabel("Rate:"));
        resultLabel = new JLabel("N/A");
        resultPanel.add(resultLabel);


        this.add(currencyPanel);
        this.add(resultPanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        back.addActionListener(
                evt -> {
                    if(evt.getSource().equals(back)) {
                       switchLoggedInController.switchToLoggedInView();
                    }
                }
        );

        ActionListener updateSelection = evt -> triggerRateQuery();
        firstCurrency.addActionListener(updateSelection);
        secondCurrency.addActionListener(updateSelection);


    }

    private void loadCurrencies(JComboBox<String> first, JComboBox<String> second) {
        try {
            String json = new String(Files.readAllBytes(Paths.get("currencies.json")));
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                first.addItem(arr.getString(i));
                second.addItem(arr.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void triggerRateQuery() {

        String from = (String) firstCurrency.getSelectedItem();
        String to   = (String) secondCurrency.getSelectedItem();

        if (from != null && to != null) {
            exchangeController.getExchangeRate(from, to);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("exchangeRate".equals(evt.getPropertyName())) {
            resultLabel.setText(evt.getNewValue().toString());
        }

    }

    public String getViewName() { return viewName; }

    public void setSwitchLoggedInController (SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }

    public void setExchangeController(ExchangeController exchangeController) {
        this.exchangeController = exchangeController;

        triggerRateQuery();
    }
}
