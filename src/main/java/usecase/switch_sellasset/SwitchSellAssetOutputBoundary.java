package usecase.switch_sellasset;

import java.util.Map;

public interface SwitchSellAssetOutputBoundary {

    void switchToSellAssetView(String username, String[] portfolios, Map<String, String[]> portfolioStocks);
}
