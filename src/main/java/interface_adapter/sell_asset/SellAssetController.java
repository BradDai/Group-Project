package interface_adapter.sell_asset;

import use_case.sell_asset.SellAssetInputBoundary;
import use_case.sell_asset.SellAssetInputData;

public class SellAssetController {
    private final SellAssetInputBoundary sellAssetInteractor;

    public SellAssetController(SellAssetInputBoundary sellAssetInteractor) {
        this.sellAssetInteractor = sellAssetInteractor;
    }

    public void execute(String portfolioName, String stockName, double quantity) {
        final SellAssetInputData sellAssetInputData = new SellAssetInputData(portfolioName, stockName, quantity);

        sellAssetInteractor.execute(sellAssetInputData);
    }

    public void fetchPrice(String stockName) {
        sellAssetInteractor.fetchPrice(stockName);
    }
}
