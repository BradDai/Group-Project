//package interface_adapter.buyasset;
//
//import use_case.buyasset.BuyAssetInputBoundary;
//import use_case.buyasset.BuyAssetInputData;
//
//public class BuyAssetController {
//
//    private final BuyAssetInputBoundary interactor;
//
//    public BuyAssetController(BuyAssetInputBoundary interactor) {
//        this.interactor = interactor;
//    }
//
//    public void execute(String username, String portfolioName, String symbol, int quantity, double price) {
//
//        BuyAssetInputData input =
//                new BuyAssetInputData(username, portfolioName, symbol, quantity, price);
//
//        interactor.execute(input);
//    }
//}

package interface_adapter.buyasset;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.buyasset.BuyAssetInputBoundary;
import use_case.buyasset.BuyAssetInputData;

public class BuyAssetController {

    private final BuyAssetInputBoundary interactor;
    private final LoggedInViewModel loggedInViewModel;

    // NOTE: new constructor takes LoggedInViewModel as well
    public BuyAssetController(BuyAssetInputBoundary interactor,
                              LoggedInViewModel loggedInViewModel) {
        this.interactor = interactor;
        this.loggedInViewModel = loggedInViewModel;
    }

    // View now only needs to pass symbol, quantity, price
    public void execute(String username, String portfolioName,
                        String symbol, int quantity, double price) {

        BuyAssetInputData input =
                new BuyAssetInputData(username, portfolioName, symbol, quantity, price);

        interactor.execute(input);
    }

}
