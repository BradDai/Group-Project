package view;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.buyasset.BuyAssetViewModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BuyAssetView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "buyasset";
    private final BuyAssetViewModel buyAssetViewModel;
    private SwitchLoggedInController switchLoggedInController;

    private final JButton back;

    // drop down menu
    private final JComboBox<String> assetComboBox;
    private final JLabel priceLabel;
    private final JComboBox<Integer> quantityComboBox;
    private final JLabel totalLabel;
    private final JButton purchaseButton;

    // Lawrence's key
    private static final String API_KEY = "ebcea301f0ad46579daa6b6dea349164";

    public BuyAssetView(BuyAssetViewModel buyAssetViewModel) {
        this.buyAssetViewModel = buyAssetViewModel;
        this.buyAssetViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Back button
        JPanel buttons = new JPanel();
        back = new JButton("Back");
        buttons.add(back);
        this.add(buttons);

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(back)) {
                    if (switchLoggedInController != null) {
                        switchLoggedInController.switchToLoggedInView();}}
            }
        });


        JPanel assetMenu = new JPanel();
        assetMenu.add(new JLabel("Choose asset:"));
        String[] assetSymbols = {"", "AAPL", "TSLA", "MSFT", "BTC/USD"};
        assetComboBox = new JComboBox<>(assetSymbols);
        assetMenu.add(assetComboBox);
        this.add(assetMenu);

        JPanel quantityMenu = new JPanel();
        quantityMenu.add(new JLabel("Quantity:"));
        Integer[] quantities = {null, 1, 2, 3, 4, 5, 10};
        quantityComboBox = new JComboBox<>(quantities);
        quantityMenu.add(quantityComboBox);
        this.add(quantityMenu);


        priceLabel = new JLabel("Price: -");
        this.add(priceLabel);
        totalLabel = new JLabel("Total: -");
        this.add(totalLabel);
        purchaseButton = new JButton("Purchase");
        this.add(purchaseButton);


        assetComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {onAssetSelected();}
        });
        quantityComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {updateTotal();}
        });
        purchaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {onPurchase();}
        });
    }


    private void onAssetSelected() {
        String symbol = (String) assetComboBox.getSelectedItem();
        if (symbol == null || symbol.isEmpty()) {
            return;}
        try {
            double price = fetchPriceFromApi(symbol);
            currentPrice = price;
            priceLabel.setText("Price: " + price);
            updateTotal();}
        catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to get price.");
            priceLabel.setText("Price: error");
            currentPrice = 0.0;
            updateTotal();}
    }

    private void onPurchase() {
        String symbol = (String) assetComboBox.getSelectedItem();
        Integer qty = (Integer) quantityComboBox.getSelectedItem();

        if (symbol == null || symbol.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please choose an asset >:(");
            return;}
        if (qty == null) {
            JOptionPane.showMessageDialog(this,
                    "Please choose a quantity >:(");
            return;}
        if (currentPrice <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Price is not loaded or invalid :(");
            return;}

        double total = currentPrice * qty;
        JOptionPane.showMessageDialog(this,
                "Purchased " + qty + " of " + symbol + " for total " + total + ".");}


    private double currentPrice = 0.0;

    private double fetchPriceFromApi(String symbol) throws IOException {
        String urlString = "https://api.twelvedata.com/price?symbol="
                + symbol + "&apikey=" + API_KEY;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int status = connection.getResponseCode();
        if (status != 200) {
            throw new IOException("HTTP " + status);}
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);}
        in.close();
        connection.disconnect();
        String body = response.toString();
        int priceIndex = body.indexOf("\"price\"");
        if (priceIndex == -1) {
            throw new IOException("No price.");}
        int colonIndex = body.indexOf(':', priceIndex);
        int startIndex = body.indexOf('"', colonIndex);
        int endIndex = body.indexOf('"', startIndex + 1);
        if (startIndex == -1 || endIndex == -1) {
            throw new IOException("Error");}
        String priceStr = body.substring(startIndex + 1, endIndex);
        return Double.parseDouble(priceStr);
    }

    private void updateTotal() {
        Integer qty = (Integer) quantityComboBox.getSelectedItem();
        if (qty == null || currentPrice <= 0) {
            totalLabel.setText("Total: -");}
        else {
            double total = currentPrice * qty;
            totalLabel.setText("Total: " + total);}
    }

    public void actionPerformed(ActionEvent evt) {}
    public void propertyChange(PropertyChangeEvent evt) {}
    public String getViewName() {return viewName;}
    public void setSwitchLoggedInController(SwitchLoggedInController controller) {
        this.switchLoggedInController = controller;
    }
}
