package interface_adapter.buyasset;

import use_case.buyasset.BuyAssetInputBoundary;
import use_case.buyasset.BuyAssetInputData;

public class BuyAssetController {

    private final BuyAssetInputBoundary interactor;

    public BuyAssetController(BuyAssetInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String username, String portfolioName, String symbol, int quantity, double price) {

        BuyAssetInputData input =
                new BuyAssetInputData(username, portfolioName, symbol, quantity, price);

        interactor.execute(input);
    }
}