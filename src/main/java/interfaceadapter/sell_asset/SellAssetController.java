package interfaceadapter.sell_asset;

import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.sell_asset.SellAssetInputBoundary;
import usecase.sell_asset.SellAssetInputData;

public class SellAssetController {
    private final SellAssetInputBoundary sellAssetInteractor;
    private final LoggedInViewModel loggedInViewModel;

    public SellAssetController(final SellAssetInputBoundary sellAssetInteractor,
                               final LoggedInViewModel loggedInViewModel) {
        this.sellAssetInteractor = sellAssetInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    // username now comes from LoggedInViewModel
    public void execute(final String portfolioName,
                        final String stockName,
                        final double quantity) {

        final String userName = loggedInViewModel.getState().getUsername();

        final SellAssetInputData sellAssetInputData =
            new SellAssetInputData(userName, portfolioName, stockName, quantity);

        sellAssetInteractor.execute(sellAssetInputData);
    }

    public void fetchPrice(final String stockName) {
        sellAssetInteractor.fetchPrice(stockName);
    }
}
