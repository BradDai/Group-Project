package usecase.sell_asset;

public interface SellAssetDataAccessInterface {
    /**
     * Get the available portfolios under the user 'username'.
     *
     * @param username the username
     * @return         available portfolios
     */
    String[] getAvailablePortfolios(String username);

    /**
     * Get the available stocks from the portfolio.
     *
     * @param username      the username
     * @param portfolioName the portfolio name
     * @return              available stocks
     */
    String[] getAvailableStocks(String username, String portfolioName);

    /**
     * Get the quantity of the selected stock.
     *
     * @param username      the username
     * @param portfolioName the portfolio name
     * @param stockName     the stock name
     * @return              quantity of the stock
     */
    double getStockQuantity(String username, String portfolioName, String stockName);

    /**
     * Update the selected stock with new quantity.
     *
     * @param username      the username
     * @param portfolioName the portfolio name
     * @param stockName     the stock name
     * @param quantity      new quantity
     */
    void updateStockQuantity(String username, String portfolioName, String stockName, double quantity);

    /**
     * Remove the selected stock from database.
     *
     * @param username      the username
     * @param portfolioName the portfolio name
     * @param stockName     the stock name
     */
    void removeStock(String username, String portfolioName, String stockName);

    /**
     * Add amount of cash to the portfolio.
     *
     * @param username      the username
     * @param portfolioName the portfolio name
     * @param amount        the amount of cash needed to add
     */
    void addCashToPortfolio(String username, String portfolioName, double amount);
}