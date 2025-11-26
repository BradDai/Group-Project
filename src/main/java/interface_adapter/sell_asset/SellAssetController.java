package interface_adapter.sell_asset;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.sell_asset.SellAssetInputBoundary;
import use_case.sell_asset.SellAssetInputData;

public class SellAssetController {
    private final SellAssetInputBoundary sellAssetInteractor;
    private final LoggedInViewModel loggedInViewModel;

    public SellAssetController(final SellAssetInputBoundary sellAssetInteractor,
                               final LoggedInViewModel loggedInViewModel) {
        this.sellAssetInteractor = sellAssetInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    // username now comes from LoggedInViewModel

    /**
     * Execute the sell asset use case.
     *
     * @param portfolioName the portfolio name
     * @param stockName     the stock name
     * @param quantity      the stock quantity
     */
    public void execute(final String portfolioName,
                        final String stockName,
                        final double quantity) {

        final String userName = loggedInViewModel.getState().getUsername();

        final SellAssetInputData sellAssetInputData =
                new SellAssetInputData(userName, portfolioName, stockName, quantity);

        sellAssetInteractor.execute(sellAssetInputData);
    }

    /**
     * Fetch the price for selected stock.
     *
     * @param stockName     the stock name
     */
    public void fetchPrice(final String stockName) {
        sellAssetInteractor.fetchPrice(stockName);
    }
}
