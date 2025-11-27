package usecase.transfer;

import java.util.List;

import entity.SubAccount;

/**
 * DAO for the Transfer Use Case.
 */
public interface TransferDataAccessInterface {

    /**
     * Checks if a portfolio exists for the user.
     *
     * @param username    the username of the account owner
     * @param portfolioId the ID of the portfolio
     * @return true if the portfolio exists, false otherwise
     */
    boolean portfolioExists(String username, String portfolioId);

    /**
     * Checks if a portfolio contains a specific asset.
     *
     * @param username    the username of the account owner
     * @param portfolioId the ID of the portfolio
     * @param assetSymbol the symbol of the asset to check
     * @return true if the asset exists in the portfolio, false otherwise
     */
    boolean hasAsset(String username, String portfolioId, String assetSymbol);

    /**
     * Gets the balance of a specific asset in a portfolio.
     *
     * @param username    the username of the account owner
     * @param portfolioId the ID of the portfolio
     * @param assetSymbol the symbol of the asset
     * @return the asset balance
     */
    double getAssetBalance(String username, String portfolioId, String assetSymbol);

    /**
     * Transfers an asset between portfolios.
     *
     * @param username      the username of the account owner
     * @param fromPortfolio the portfolio sending the asset
     * @param toPortfolio   the portfolio receiving the asset
     * @param assetSymbol   the symbol of the asset being transferred
     * @param amount        the amount to transfer
     */
    void transferAsset(String username, String fromPortfolio, String toPortfolio,
                       String assetSymbol, double amount);

    /**
     * Gets all available portfolios for the current user.
     *
     * @param username the username of the account owner
     * @return an array of portfolio IDs
     */
    String[] getAvailablePortfolios(String username);

    /**
     * Gets all stock symbols available in a portfolio.
     *
     * @param username    the username of the account owner
     * @param portfolioId the ID of the portfolio
     * @return an array of stock symbols
     */
    String[] getAvailableStocks(String username, String portfolioId);

    /**
     * Gets all currency types available in a portfolio.
     *
     * @param username    the username of the account owner
     * @param portfolioId the ID of the portfolio
     * @return an array of currency type symbols
     */
    String[] getAvailableCurrencies(String username, String portfolioId);

    /**
     * Gets the list of all subaccounts that belong to a user.
     *
     * @param username the username of the account owner
     * @return a list of subaccounts
     */
    List<SubAccount> getSubAccountsOf(String username);
}
