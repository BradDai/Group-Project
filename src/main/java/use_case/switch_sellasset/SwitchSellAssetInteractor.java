package use_case.switch_sellasset;

import use_case.sell_asset.SellAssetDataAccessInterface;

public class SwitchSellAssetInteractor implements SwitchSellAssetInputBoundary {

    private final SwitchSellAssetOutputBoundary sellAssetPresenter;
    private final SellAssetDataAccessInterface dataAccess;

    public SwitchSellAssetInteractor(SwitchSellAssetOutputBoundary sellAssetOutputBoundary, SellAssetDataAccessInterface dataAccess) {
        this.sellAssetPresenter = sellAssetOutputBoundary;
        this.dataAccess = dataAccess;
    }

    public void switchToSellAssetView(String username) {
        String[] portfolios = dataAccess.getAvailablePortfolios(username);
        String[] stocks = dataAccess.getAvailableStocks(username, portfolios[1]); //TODO: change to current selected portfolio.
        sellAssetPresenter.switchToSellAssetView(username, portfolios);
    }
}
