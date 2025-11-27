package app;

import dataaccess.FileSubAccountDataAccessJSON;
import dataaccess.TransactionDataAccessInterface;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.exchange.ExchangeController;
import interfaceadapter.exchange.ExchangePresenter;
import interfaceadapter.transfer.TransferController;
import interfaceadapter.transfer.TransferPresenter;
import usecase.exchange.ExchangeInputBoundary;
import usecase.exchange.ExchangeInteractor;
import usecase.exchange.ExchangeOutputBoundary;
import usecase.transfer.TransferInputBoundary;
import usecase.transfer.TransferInteractor;
import usecase.transfer.TransferOutputBoundary;

/**
 * Wires exchange and transfer use cases.
 */
public class MoneyUseCaseConfigurator {

    private final ViewManagerModel viewManagerModel;
    private final FileSubAccountDataAccessJSON subAccountDataAccess;
    private final TransactionDataAccessInterface transactionDataAccessObject;
    private final ViewConfigurator views;

    public MoneyUseCaseConfigurator(
            final ViewManagerModel viewManagerModel,
            final FileSubAccountDataAccessJSON subAccountDataAccess,
            final TransactionDataAccessInterface transactionDataAccessObject,
            final ViewConfigurator views
    ) {
        this.viewManagerModel = viewManagerModel;
        this.subAccountDataAccess = subAccountDataAccess;
        this.transactionDataAccessObject = transactionDataAccessObject;
        this.views = views;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        addExchangeUseCase();
        addTransferUseCase();
    }

    private void addExchangeUseCase() {
        ExchangeOutputBoundary outputBoundary =
                new ExchangePresenter(
                        views.getMoneyViews().getExchangeViewModel(),
                        views.getLoggedInViews().getLoggedInViewModel()
                );

        ExchangeInputBoundary interactor =
                new ExchangeInteractor(
                        outputBoundary,
                        subAccountDataAccess,
                        transactionDataAccessObject
                );

        ExchangeController controller =
                new ExchangeController(
                        interactor,
                        views.getLoggedInViews().getLoggedInViewModel()
                );

        views.getMoneyViews().getExchangeView().setExchangeController(controller);
    }

    private void addTransferUseCase() {
        TransferOutputBoundary outputBoundary =
                new TransferPresenter(
                        views.getMoneyViews().getTransferViewModel(),
                        views.getLoggedInViews().getLoggedInViewModel(),
                        viewManagerModel
                );

        TransferInputBoundary interactor =
                new TransferInteractor(
                        subAccountDataAccess,
                        outputBoundary,
                        transactionDataAccessObject
                );

        TransferController controller =
                new TransferController(interactor);
        views.getMoneyViews().getTransferView().setTransferController(controller);
    }
}
