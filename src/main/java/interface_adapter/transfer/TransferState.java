package interface_adapter.transfer;

import java.util.Map;

public class TransferState {
    private String username = "";
    private String fromPortfolio = "";
    private String toPortfolio = "";
    private String transferType = "Currency";
    private String fromBalance = "0.00";
    private String toBalance = "0.00";
    private String[] availablePortfolios = new String[0];
    private String[] availableStocks = new String[0];
    private String[] availableCurrencies = new String[]{"USD"};
    private Map<String, Double> stockPrices;
    private Map<String, Double> currencyBalances;
    private String error = "";

    public TransferState() {
    }

    public TransferState(TransferState copy) {
        this.username = copy.username;
        this.fromPortfolio = copy.fromPortfolio;
        this.toPortfolio = copy.toPortfolio;
        this.transferType = copy.transferType;
        this.fromBalance = copy.fromBalance;
        this.toBalance = copy.toBalance;
        this.availablePortfolios = copy.availablePortfolios;
        this.availableStocks = copy.availableStocks;
        this.availableCurrencies = copy.availableCurrencies;
        this.stockPrices = copy.stockPrices;
        this.currencyBalances = copy.currencyBalances;
        this.error = copy.error;
    }

    public String getFromBalance() { return fromBalance; }
    public void setFromBalance(String fromBalance) { this.fromBalance = fromBalance; }

    public String getToBalance() { return toBalance; }
    public void setToBalance(String toBalance) { this.toBalance = toBalance; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFromPortfolio() { return fromPortfolio; }
    public void setFromPortfolio(String fromPortfolio) { this.fromPortfolio = fromPortfolio; }

    public String getToPortfolio() { return toPortfolio; }
    public void setToPortfolio(String toPortfolio) { this.toPortfolio = toPortfolio; }

    public String getTransferType() { return transferType; }
    public void setTransferType(String transferType) { this.transferType = transferType; }

    public String[] getAvailablePortfolios() { return availablePortfolios; }
    public void setAvailablePortfolios(String[] availablePortfolios) { this.availablePortfolios = availablePortfolios; }

    public String[] getAvailableStocks() { return availableStocks; }
    public void setAvailableStocks(String[] availableStocks) { this.availableStocks = availableStocks; }

    public String[] getAvailableCurrencies() { return availableCurrencies; }
    public void setAvailableCurrencies(String[] availableCurrencies) { this.availableCurrencies = availableCurrencies; }

    public Map<String, Double> getStockPrices() { return stockPrices; }
    public void setStockPrices(Map<String, Double> stockPrices) { this.stockPrices = stockPrices; }

    public double getStockPrice(String symbol) {
        if (stockPrices == null || !stockPrices.containsKey(symbol)) { return 0.0; }
        return stockPrices.get(symbol);
    }

    public Map<String, Double> getCurrencyBalances() { return currencyBalances; }
    public void setCurrencyBalances(Map<String, Double> currencyBalances) { this.currencyBalances = currencyBalances; }

    public double getCurrencyBalance(String currency) {
        if (currencyBalances == null || !currencyBalances.containsKey(currency)) { return 0.0; }
        return currencyBalances.get(currency);
    }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}