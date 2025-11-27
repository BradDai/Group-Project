package interfaceadapter.transfer;

/**
 * Represents the state of the Transfer View.
 * This class stores all user selections, balances, available assets, and error messages
 * needed by the UI. It is updated by the presenter and observed by the view.
 */
public class TransferState {

    /**
     * The username of the logged-in user.
     */
    private String username = "";

    /**
     * The balance available in the sender portfolio.
     */
    private String fromBalance = "0.00";

    /**
     * The balance available in the receiver portfolio.
     */
    private String toBalance = "0.00";

    /**
     * The amount to transfer.
     */
    private String amount = "";

    /**
     * The list of available portfolios for the user.
     */
    private String[] availablePortfolios = new String[0];

    /**
     * The list of stocks available in the selected portfolio.
     */
    private String[] availableStocks = new String[0];

    /**
     * The list of currencies available in the selected portfolio.
     */
    private String[] availableCurrencies = new String[] {"USD"};

    /**
     * An error message, if one is present.
     */
    private String error = "";

    /**
     * Creates an empty TransferState with default values.
     */
    public TransferState() {
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
     * Sets the "from" portfolio.
     *
     */
    public void setFromPortfolio() {
    }

    /**
     * Sets the "to" portfolio.
     *
     */
    public void setToPortfolio() {
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
