package interface_adapter.transfer;

import java.util.Map;

public class TransferState {
    private String username = "";
    private String fromPortfolio = "";
    private String toPortfolio = "";
    private String transferType = "Currency";
    private String fromBalance = "0.00";
    private String toBalance = "0.00";
    private String amount = "";
    private String[] availablePortfolios = new String[0];
    private String[] availableStocks = new String[0];
    private String[] availableCurrencies = new String[] {"USD"};
    private Map<String, Double> stockPrices;
    private Map<String, Double> currencyBalances;
    private String error = "";

    public TransferState() {
    }

    public TransferState(final TransferState copy) {
        this.username = copy.username;
        this.fromPortfolio = copy.fromPortfolio;
        this.toPortfolio = copy.toPortfolio;
        this.transferType = copy.transferType;
        this.fromBalance = copy.fromBalance;
        this.toBalance = copy.toBalance;
        this.amount = copy.amount;
        this.availablePortfolios = copy.availablePortfolios;
        this.availableStocks = copy.availableStocks;
        this.availableCurrencies = copy.availableCurrencies;
        this.stockPrices = copy.stockPrices;
        this.currencyBalances = copy.currencyBalances;
        this.error = copy.error;
    }

    // Getters and Setters
    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getFromBalance() {
        return fromBalance;
    }

    public void setFromBalance(final String fromBalance) {
        this.fromBalance = fromBalance;
    }

    public String getToBalance() {
        return toBalance;
    }

    public void setToBalance(final String toBalance) {
        this.toBalance = toBalance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getFromPortfolio() {
        return fromPortfolio;
    }

    public void setFromPortfolio(final String fromPortfolio) {
        this.fromPortfolio = fromPortfolio;
    }

    public String getToPortfolio() {
        return toPortfolio;
    }

    public void setToPortfolio(final String toPortfolio) {
        this.toPortfolio = toPortfolio;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(final String transferType) {
        this.transferType = transferType;
    }

    public String[] getAvailablePortfolios() {
        return availablePortfolios;
    }

    public void setAvailablePortfolios(final String[] availablePortfolios) {
        this.availablePortfolios = availablePortfolios;
    }

    public String[] getAvailableStocks() {
        return availableStocks;
    }

    public void setAvailableStocks(final String[] availableStocks) {
        this.availableStocks = availableStocks;
    }

    public String[] getAvailableCurrencies() {
        return availableCurrencies;
    }

    public void setAvailableCurrencies(final String[] availableCurrencies) {
        this.availableCurrencies = availableCurrencies;
    }

    public Map<String, Double> getStockPrices() {
        return stockPrices;
    }

    public void setStockPrices(final Map<String, Double> stockPrices) {
        this.stockPrices = stockPrices;
    }

    public double getStockPrice(final String symbol) {
        if (stockPrices == null || !stockPrices.containsKey(symbol)) {
            return 0.0;
        }
        return stockPrices.get(symbol);
    }

    public Map<String, Double> getCurrencyBalances() {
        return currencyBalances;
    }

    public void setCurrencyBalances(final Map<String, Double> currencyBalances) {
        this.currencyBalances = currencyBalances;
    }

    public double getCurrencyBalance(final String currency) {
        if (currencyBalances == null || !currencyBalances.containsKey(currency)) {
            return 0.0;
        }
        return currencyBalances.get(currency);
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}
