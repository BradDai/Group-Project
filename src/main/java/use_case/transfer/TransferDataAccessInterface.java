package use_case.transfer;

import entity.SubAccount;
import entity.transaction.Transaction;

import java.util.List;

/**
 * DAO for the Transfer Use Case.
 */
public interface TransferDataAccessInterface {
    /**
     * Checks if a portfolio exists for the user.
     */
    boolean portfolioExists(String username, String portfolioId);

    /**
     * Checks if a portfolio contains a specific asset.
     */
    boolean hasAsset(String username, String portfolioId, String assetSymbol);

    /**
     * Gets the balance of a specific asset in a portfolio.
     */
    double getAssetBalance(String username, String portfolioId, String assetSymbol);

    /**
     * Transfers an asset between portfolios.
     */
    void transferAsset(String username, String fromPortfolio, String toPortfolio,
                       String assetSymbol, double amount);

    /**
     * Saves a transaction to the transaction history.
     */
    void saveTransaction(Transaction transaction);

    /**
     * Gets all available portfolios for the current user.
     */
    String[] getAvailablePortfolios(String username);

    /**
     * Gets all stock symbols available in a portfolio.
     */
    String[] getAvailableStocks(String username, String portfolioId);

    /**
     * Gets all currency types available in a portfolio.
     */
    String[] getAvailableCurrencies(String username, String portfolioId);

    /**
     * Gets the current price of a stock.
     */
    double getStockPrice(String symbol);

    List<SubAccount> getSubAccountsOf(String username);
}