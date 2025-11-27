package app;

import dataaccess.FileSubAccountDataAccessJSON;
import dataaccess.TransactionDataAccessInterface;
import interfaceadapter.sell_asset.SellAssetController;
import interfaceadapter.sell_asset.SellAssetPresenter;
import usecase.sell_asset.SellAssetInputBoundary;
import usecase.sell_asset.SellAssetInteractor;

/**
 * Wires the sell-asset use case.
 */
public class SellAssetUseCaseConfigurator {

    private final FileSubAccountDataAccessJSON subAccountDataAccess;
    private final TransactionDataAccessInterface transactionDataAccessObject;
    private final ViewConfigurator views;

    public SellAssetUseCaseConfigurator(
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
        SellAssetPresenter presenter =
                new SellAssetPresenter(
                        views.getAssetViews().getSellAssetViewModel(),
                        views.getLoggedInViews().getLoggedInViewModel(),
                        subAccountDataAccess
                );

        SellAssetInputBoundary interactor =
                new SellAssetInteractor(
                        subAccountDataAccess,
                        transactionDataAccessObject,
                        presenter,
                        presenter
                );

        SellAssetController controller =
                new SellAssetController(
                        interactor,
                        views.getLoggedInViews().getLoggedInViewModel()
                );

        views.getAssetViews().getSellAssetView().setSellAssetController(controller);
    }
}
