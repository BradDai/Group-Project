package usecase.switch_sellasset;

import java.util.HashMap;
import java.util.Map;

import usecase.sell_asset.SellAssetDataAccessInterface;

public class SwitchSellAssetInteractor implements SwitchSellAssetInputBoundary {

    private final SwitchSellAssetOutputBoundary sellAssetPresenter;
    private final SellAssetDataAccessInterface dataAccess;

    public SwitchSellAssetInteractor(final SwitchSellAssetOutputBoundary sellAssetOutputBoundary,
                                     final SellAssetDataAccessInterface dataAccess) {
        this.sellAssetPresenter = sellAssetOutputBoundary;
        this.dataAccess = dataAccess;
    }

    public void switchToSellAssetView(final String username) {
        final String[] portfolios = dataAccess.getAvailablePortfolios(username);
        final Map<String, String[]> portfolioStocks = new HashMap<>();
        for (final String p : portfolios) {
            portfolioStocks.put(p, dataAccess.getAvailableStocks(username, p));
        }
        sellAssetPresenter.switchToSellAssetView(username, portfolios, portfolioStocks);
    }
}
