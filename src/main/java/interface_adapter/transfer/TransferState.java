package interface_adapter.transfer;

import java.util.Map;

/**
 * Represents the state of the Transfer View.
 * This class stores all user selections, balances, available assets, and error messages
 * needed by the UI. It is updated by the presenter and observed by the view.
 */
public class TransferState {

    /** The username of the logged-in user. */
    private String username = "";

    /** The portfolio from which the asset is transferred. */
    private String fromPortfolio = "";

    /** The portfolio receiving the transferred asset. */
    private String toPortfolio = "";

    /** The type of transfer, e.g. "Currency" or "Stock". */
    private String transferType = "Currency";

    /** The balance available in the sender portfolio. */
    private String fromBalance = "0.00";

    /** The balance available in the receiver portfolio. */
    private String toBalance = "0.00";

    /** The amount to transfer. */
    private String amount = "";

    /** The list of available portfolios for the user. */
    private String[] availablePortfolios = new String[0];

    /** The list of stocks available in the selected portfolio. */
    private String[] availableStocks = new String[0];

    /** The list of currencies available in the selected portfolio. */
    private String[] availableCurrencies = new String[] {"USD"};

    /** A map of stock symbols to their current prices. */
    private Map<String, Double> stockPrices;

    /** A map of currency symbols to their balances in the selected portfolio. */
    private Map<String, Double> currencyBalances;

    /** An error message, if one is present. */
    private String error = "";

    /**
     * Creates an empty TransferState with default values.
     */
    public TransferState() {
    }

    /**
     * Copy constructor.
     *
     * @param copy the state to duplicate
     */
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

    /**
     * Returns the amount entered by the user.
     *
     * @return the transfer amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the amount to transfer.
     *
     * @param amount the transfer amount
     */
    public void setAmount(final String amount) {
        this.amount = amount;
    }

    /**
     * Returns the sender portfolio's balance.
     *
     * @return the balance as a string
     */
    public String getFromBalance() {
        return fromBalance;
    }

    /**
     * Sets the sender portfolio's balance.
     *
     * @param fromBalance the balance string
     */
    public void setFromBalance(final String fromBalance) {
        this.fromBalance = fromBalance;
    }

    /**
     * Returns the receiver portfolio's balance.
     *
     * @return the balance as a string
     */
    public String getToBalance() {
        return toBalance;
    }

    /**
     * Sets the receiver portfolio's balance.
     *
     * @param toBalance the balance string
     */
    public void setToBalance(final String toBalance) {
        this.toBalance = toBalance;
    }

    /**
     * Returns the current username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with this state.
     *
     * @param username the user’s name
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Returns the "from" portfolio ID.
     *
     * @return the portfolio ID
     */
    public String getFromPortfolio() {
        return fromPortfolio;
    }

    /**
     * Sets the "from" portfolio.
     *
     * @param fromPortfolio the portfolio ID
     */
    public void setFromPortfolio(final String fromPortfolio) {
        this.fromPortfolio = fromPortfolio;
    }

    /**
     * Returns the "to" portfolio ID.
     *
     * @return the portfolio ID
     */
    public String getToPortfolio() {
        return toPortfolio;
    }

    /**
     * Sets the "to" portfolio.
     *
     * @param toPortfolio the portfolio ID
     */
    public void setToPortfolio(final String toPortfolio) {
        this.toPortfolio = toPortfolio;
    }

    /**
     * Returns the selected transfer type (e.g., "Currency" or "Stock").
     *
     * @return the transfer type
     */
    public String getTransferType() {
        return transferType;
    }

    /**
     * Sets the transfer type.
     *
     * @param transferType the type of transfer
     */
    public void setTransferType(final String transferType) {
        this.transferType = transferType;
    }

    /**
     * Returns the list of available portfolios.
     *
     * @return an array of portfolio IDs
     */
    public String[] getAvailablePortfolios() {
        return availablePortfolios;
    }

    /**
     * Sets the list of available portfolios.
     *
     * @param availablePortfolios the list of portfolio IDs
     */
    public void setAvailablePortfolios(final String[] availablePortfolios) {
        this.availablePortfolios = availablePortfolios;
    }

    /**
     * Returns the list of available stocks.
     *
     * @return an array of stock symbols
     */
    public String[] getAvailableStocks() {
        return availableStocks;
    }

    /**
     * Sets the list of available stocks.
     *
     * @param availableStocks the stock symbols available
     */
    public void setAvailableStocks(final String[] availableStocks) {
        this.availableStocks = availableStocks;
    }

    /**
     * Returns the list of available currencies.
     *
     * @return an array of currency codes
     */
    public String[] getAvailableCurrencies() {
        return availableCurrencies;
    }

    /**
     * Sets the list of available currencies.
     *
     * @param availableCurrencies the currency codes available
     */
    public void setAvailableCurrencies(final String[] availableCurrencies) {
        this.availableCurrencies = availableCurrencies;
    }

    /**
     * Returns the map of stock prices.
     *
     * @return a map of symbol → price
     */
    public Map<String, Double> getStockPrices() {
        return stockPrices;
    }

    /**
     * Sets the stock price map.
     *
     * @param stockPrices a map of symbol → price
     */
    public void setStockPrices(final Map<String, Double> stockPrices) {
        this.stockPrices = stockPrices;
    }

    /**
     * Returns the price of a given stock symbol.
     *
     * @param symbol the stock symbol
     * @return the stock price, or 0.0 if not found
     */
    public double getStockPrice(final String symbol) {
        double result = 0.0;
        if (stockPrices != null && stockPrices.containsKey(symbol)) {
            result = stockPrices.get(symbol);
        }
        return result;
    }

    /**
     * Returns the map of currency balances.
     *
     * @return a map of currency → balance
     */
    public Map<String, Double> getCurrencyBalances() {
        return currencyBalances;
    }

    /**
     * Sets the currency balance map.
     *
     * @param currencyBalances a map of currency → balance
     */
    public void setCurrencyBalances(final Map<String, Double> currencyBalances) {
        this.currencyBalances = currencyBalances;
    }

    /**
     * Returns the balance of a given currency.
     *
     * @param currency the currency code
     * @return the balance, or 0.0 if not found
     */
    public double getCurrencyBalance(final String currency) {
        double result = 0.0;
        if (currencyBalances != null && currencyBalances.containsKey(currency)) {
            result = currencyBalances.get(currency);
        }
        return result;
    }

    /**
     * Returns the current error message.
     *
     * @return the error message, or empty string if none
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the current error message.
     *
     * @param error the error message
     */
    public void setError(final String error) {
        this.error = error;
    }
}
