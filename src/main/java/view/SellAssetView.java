package view;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.sellasset.SellAssetViewModel;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SellAssetView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "sellasset";
    private final SellAssetViewModel sellAssetViewModel;
    private SwitchLoggedInController switchLoggedInController;

    private final JButton back;
    private final JButton confirm;

    // variables for functionality
    private JComboBox<String> portfolioSelector;
    private JComboBox<String> stockSelector;

    private JLabel stockPriceLabel;
    private JTextField quantityField;
    private JLabel totalPriceLabel;

    private double currentStockPrice = 0.0;

    public SellAssetView(SellAssetViewModel sellAssetViewModel) {
        this.sellAssetViewModel = sellAssetViewModel;
        this.sellAssetViewModel.addPropertyChangeListener(this);

        final JPanel PortfolioPanel = new JPanel();
        PortfolioPanel.add(new JLabel("Select portfolio:"));
        portfolioSelector = new JComboBox<>(new String[]{"Portfolio 1", "Portfolio 2", "Portfolio 3"});
        PortfolioPanel.add(portfolioSelector);
        this.add(PortfolioPanel);

        final JPanel StockPanel = new JPanel();
        StockPanel.add(new JLabel("Select stock:"));
        stockSelector = new JComboBox<>(new String[]{"AAPL", "TSLA", "MSFT"});
        StockPanel.add(stockSelector);
        this.add(StockPanel);

        final JPanel StockPricePanel = new JPanel();
        StockPricePanel.add(new JLabel("Stock price:"));
        stockPriceLabel = new JLabel("—");  // placeholder
        StockPricePanel.add(stockPriceLabel);
        this.add(StockPricePanel);

        final JPanel QuantityPanel = new JPanel();
        QuantityPanel.add(new JLabel("Quantity to sell:"));
        quantityField = new JTextField(10);
        QuantityPanel.add(quantityField);
        this.add(QuantityPanel);

        final JPanel TotalPricePanel = new JPanel();
        TotalPricePanel.add(new JLabel("Total price:"));
        totalPriceLabel = new JLabel("—");
        TotalPricePanel.add(totalPriceLabel);
        this.add(TotalPricePanel);

        final JPanel Buttons = new JPanel();
        confirm = new JButton("confirm");
        back = new JButton("Back");
        Buttons.add(confirm);
        Buttons.add(back);
        this.add(Buttons);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        stockSelector.addActionListener(e -> {
            String ticker = (String) stockSelector.getSelectedItem();
            fetchAndDisplayPrice(ticker);
        });

        back.addActionListener(
                evt -> {
                    if(evt.getSource().equals(back)) {
                        switchLoggedInController.switchToLoggedInView();
                    }
                }
        );

        // listener
        quantityField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotal(); }
        });
    }

    private void updateTotal() {
        try {
            double qty = Double.parseDouble(quantityField.getText());
            double total = qty * currentStockPrice;
            totalPriceLabel.setText(String.format("%.2f", total));
        } catch (Exception ex) {
            totalPriceLabel.setText("—");
        }
    }

    // fetch the price using API
    private void fetchAndDisplayPrice(String ticker) {
        // TODO: replace with real API key
        String apiKey = "demo";

        try {
            String url = "https://api.twelvedata.com/price?symbol=" + ticker + "&apikey=" + apiKey;

            java.net.URL requestUrl = new java.net.URL(url);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");

            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();

            org.json.JSONObject json = new org.json.JSONObject(response.toString());
            currentStockPrice = json.getDouble("price");

            stockPriceLabel.setText("$" + currentStockPrice);

            updateTotal();

        } catch (Exception e) {
            stockPriceLabel.setText("API Error");
            currentStockPrice = 0.0;
            updateTotal();
            e.printStackTrace();
        }
    }
    // end of method

    public void actionPerformed(ActionEvent evt) { System.out.println("Click " + evt.getActionCommand()); }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public String getViewName() { return viewName; }

    public void setSwitchLoggedInController(SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }
}
