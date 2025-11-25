//package interface_adapter.sell_asset;
//
//import use_case.sell_asset.SellAssetInputBoundary;
//import use_case.sell_asset.SellAssetInputData;
//
//public class SellAssetController {
//    private final SellAssetInputBoundary sellAssetInteractor;
//
//    public SellAssetController(final SellAssetInputBoundary sellAssetInteractor) {
//        this.sellAssetInteractor = sellAssetInteractor;
//    }
//
//    public void execute(final String userName, final String portfolioName, final String stockName, final double quantity) {
//        final SellAssetInputData sellAssetInputData =
//            new SellAssetInputData(userName, portfolioName, stockName, quantity);
//
//        sellAssetInteractor.execute(sellAssetInputData);
//    }
//
//    public void fetchPrice(final String stockName) {
//        sellAssetInteractor.fetchPrice(stockName);
//    }
//}
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
