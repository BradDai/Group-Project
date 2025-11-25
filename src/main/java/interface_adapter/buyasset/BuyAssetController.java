package interface_adapter.buyasset;

import use_case.buyasset.BuyAssetInputBoundary;
import use_case.buyasset.BuyAssetInputData;

public class BuyAssetController {

    private final BuyAssetInputBoundary interactor;

    public BuyAssetController(final BuyAssetInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(final String username, final String portfolioName, final String symbol, final int quantity, final double price) {

        final BuyAssetInputData input =
            new BuyAssetInputData(username, portfolioName, symbol, quantity, price);

        interactor.execute(input);
    }
}
