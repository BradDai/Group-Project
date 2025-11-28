package app;

import dataaccess.FileSubAccountDataAccessJSON;
import dataaccess.TransactionDataAccessInterface;
import interfaceadapter.buyasset.BuyAssetController;
import interfaceadapter.buyasset.BuyAssetPresenter;
import usecase.buyasset.BuyAssetInputBoundary;
import usecase.buyasset.BuyAssetInteractor;

/**
 * Wires the buy-asset use case.
 */
public class BuyAssetUseCaseConfigurator {

    private final FileSubAccountDataAccessJSON subAccountDataAccess;
    private final TransactionDataAccessInterface transactionDataAccessObject;
    private final ViewConfigurator views;

    public BuyAssetUseCaseConfigurator(
            final FileSubAccountDataAccessJSON subAccountDataAccess,
            final TransactionDataAccessInterface transactionDataAccessObject,
            final ViewConfigurator views
    ) {
        this.subAccountDataAccess = subAccountDataAccess;
        this.transactionDataAccessObject = transactionDataAccessObject;
        this.views = views;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        final BuyAssetPresenter presenter =
                new BuyAssetPresenter(
                        views.getAssetViews().getBuyAssetViewModel(),
                        views.getLoggedInViews().getLoggedInViewModel(),
                        subAccountDataAccess
                );

        final BuyAssetInputBoundary interactor =
                new BuyAssetInteractor(
                        subAccountDataAccess,
                        transactionDataAccessObject,
                        presenter
                );

        final BuyAssetController controller =
                new BuyAssetController(
                        interactor,
                        views.getLoggedInViews().getLoggedInViewModel()
                );

        views.getAssetViews().getBuyAssetView().setBuyAssetController(controller);
    }
}
