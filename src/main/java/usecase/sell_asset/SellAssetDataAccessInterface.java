package usecase.sell_asset;

public interface SellAssetDataAccessInterface {
    String[] getAvailablePortfolios(String username);

    String[] getAvailableStocks(String username, String portfolioName);

    double getStockQuantity(String username, String portfolioName, String stockName);

    void updateStockQuantity(String username, String portfolioName, String stockName, double quantity);

    void removeStock(String username, String portfolioName, String stockName);

    void addCashToPortfolio(String username, String portfolioName, double amount);
}
