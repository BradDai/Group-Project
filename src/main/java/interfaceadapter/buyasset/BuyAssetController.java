package interfaceadapter.buyasset;

import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.buyasset.BuyAssetInputBoundary;
import usecase.buyasset.BuyAssetInputData;

public class BuyAssetController {

    private final BuyAssetInputBoundary interactor;

    public BuyAssetController(final BuyAssetInputBoundary interactor,
                              final LoggedInViewModel loggedInViewModel) {
        this.interactor = interactor;
    }
    /**
     * Execute.
     *
     * @param username .
     * @param portfolioName .
     * @param symbol .
     * @param quantity .
     * @param price .
     */

    public void execute(final String username, final String portfolioName,
                        final String symbol, final int quantity, final double price) {

        final BuyAssetInputData input =
            new BuyAssetInputData(username, portfolioName, symbol, quantity, price);

        interactor.execute(input);
    }

}
