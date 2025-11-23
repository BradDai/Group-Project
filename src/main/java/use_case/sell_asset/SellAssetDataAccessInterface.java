package use_case.sell_asset;

import entity.User;

public interface SellAssetDataAccessInterface {
    String[] getAvailablePortfolios(String username);
    String[] getAvailableStocks(String username, String portfolioId);
}
