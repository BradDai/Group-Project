package usecase.switch_sellasset;

import java.util.Map;

public interface SwitchSellAssetOutputBoundary {

    /**
     * L.
     * @param username .
     * @param portfolios .
     * @param portfolioStocks .
     */
    void switchToSellAssetView(String username, String[] portfolios, Map<String, String[]> portfolioStocks);
}
