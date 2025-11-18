package use_case.transfer;

import entity.transaction.Transaction;

/**
 * DAO for the Transfer Use Case.
 */
public interface TransferDataAccessInterface {
    /**
     * Checks if a portfolio exists.
     * @param portfolioId the portfolio identifier
     * @return true if the portfolio exists, false otherwise
     */
    boolean portfolioExists(String portfolioId);

    /**
     * Checks if a portfolio contains a specific asset.
     * @param portfolioId the portfolio identifier
     * @param assetSymbol the asset symbol
     * @return true if the portfolio has the asset, false otherwise
     */
    boolean hasAsset(String portfolioId, String assetSymbol);

    /**
     * Gets the balance of a specific asset in a portfolio.
     * @param portfolioId the portfolio identifier
     * @param assetSymbol the asset symbol
     * @return the balance of the asset
     */
    double getAssetBalance(String portfolioId, String assetSymbol);

    /**
     * Transfers an asset between portfolios.
     * @param fromPortfolio source portfolio
     * @param toPortfolio destination portfolio
     * @param assetSymbol asset to transfer
     * @param amount amount to transfer
     */
    void transferAsset(String fromPortfolio, String toPortfolio,
                       String assetSymbol, double amount);

    /**
     * Saves a transaction to the transaction history.
     * @param transaction the transaction to save
     */
    void saveTransaction(entity.transaction.Transaction transaction);

    /**
     * Gets all available portfolios for the current user.
     * @return array of portfolio identifiers
     */
    String[] getAvailablePortfolios();

    /**
     * Gets all stock symbols available in a portfolio.
     * @param portfolioId the portfolio identifier
     * @return array of stock symbols
     */
    String[] getAvailableStocks(String portfolioId);

    /**
     * Gets all currency types available in a portfolio.
     * @param portfolioId the portfolio identifier
     * @return array of currency symbols
     */
    String[] getAvailableCurrencies(String portfolioId);

    /**
     * Gets the current price of a stock.
     * @param symbol the stock symbol
     * @return the current price per share
     */
    double getStockPrice(String symbol);
}