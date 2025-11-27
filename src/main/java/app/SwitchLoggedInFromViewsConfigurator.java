package app;

import dataaccess.FileSubAccountDataAccessJSON;
import interfaceadapter.SwitchLoggedInController;
import interfaceadapter.SwitchLoggedInPresenter;
import interfaceadapter.ViewManagerModel;
import usecase.switch_loggedin.SwitchLoggedInInputBoundary;
import usecase.switch_loggedin.SwitchLoggedInInteractor;
import usecase.switch_loggedin.SwitchLoggedInOutputBoundary;

/**
 * Wires switch-to-logged-in use cases from various views:
 * exchange, transfer, history, buy asset, sell asset.
 */
public class SwitchLoggedInFromViewsConfigurator {

    private final ViewManagerModel viewManagerModel;
    private final FileSubAccountDataAccessJSON subAccountDataAccess;
    private final ViewConfigurator views;

    public SwitchLoggedInFromViewsConfigurator(
            final ViewManagerModel viewManagerModel,
            final FileSubAccountDataAccessJSON subAccountDataAccess,
            final ViewConfigurator views
    ) {
        this.viewManagerModel = viewManagerModel;
        this.subAccountDataAccess = subAccountDataAccess;
        this.views = views;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        addFromExchange();
        addFromTransfer();
        addFromHistory();
        addFromBuyAsset();
        addFromSellAsset();
    }

    private SwitchLoggedInController createController() {
        SwitchLoggedInOutputBoundary outputBoundary =
                new SwitchLoggedInPresenter(
                        views.getLoggedInViews().getLoggedInViewModel(),
                        viewManagerModel,
                        subAccountDataAccess
                );

        SwitchLoggedInInputBoundary interactor =
                new SwitchLoggedInInteractor(
                        outputBoundary,
                        subAccountDataAccess
                );

        return new SwitchLoggedInController(interactor);
    }

    private void addFromExchange() {
        views.getMoneyViews()
                .getExchangeView()
                .setSwitchLoggedInController(createController());
    }

    private void addFromTransfer() {
        views.getMoneyViews()
                .getTransferView()
                .setSwitchLoggedInController(createController());
    }

    private void addFromHistory() {
        views.getHistoryViews()
                .getHistoryView()
                .setSwitchLoggedInController(createController());
    }

    private void addFromBuyAsset() {
        views.getAssetViews()
                .getBuyAssetView()
                .setSwitchLoggedInController(createController());
    }

    private void addFromSellAsset() {
        views.getAssetViews()
                .getSellAssetView()
                .setSwitchLoggedInController(createController());
    }
}
