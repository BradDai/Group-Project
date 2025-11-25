package interface_adapter.sell_asset;

import use_case.sell_asset.SellAssetInputBoundary;
import use_case.sell_asset.SellAssetInputData;

public class SellAssetController {
    private final SellAssetInputBoundary sellAssetInteractor;

    public SellAssetController(final SellAssetInputBoundary sellAssetInteractor) {
        this.sellAssetInteractor = sellAssetInteractor;
    }

    public void execute(final String userName, final String portfolioName, final String stockName, final double quantity) {
        final SellAssetInputData sellAssetInputData =
            new SellAssetInputData(userName, portfolioName, stockName, quantity);

        sellAssetInteractor.execute(sellAssetInputData);
    }

    public void fetchPrice(final String stockName) {
        sellAssetInteractor.fetchPrice(stockName);
    }
}
