package interfaceadapter.buyasset;

import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.buyasset.BuyAssetInputBoundary;
import usecase.buyasset.BuyAssetInputData;

public class BuyAssetController {

    private final BuyAssetInputBoundary interactor;

    // NOTE: new constructor takes LoggedInViewModel as well
    public BuyAssetController(final BuyAssetInputBoundary interactor,
                              final LoggedInViewModel loggedInViewModel) {
        this.interactor = interactor;
    }

    // View now only needs to pass symbol, quantity, price
    public void execute(final String username, final String portfolioName,
                        final String symbol, final int quantity, final double price) {

        final BuyAssetInputData input =
            new BuyAssetInputData(username, portfolioName, symbol, quantity, price);

        interactor.execute(input);
    }

}
