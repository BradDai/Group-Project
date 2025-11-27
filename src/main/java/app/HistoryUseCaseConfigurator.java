package app;

import dataaccess.TransactionDataAccessInterface;
import interfaceadapter.history.TransactionHistoryController;
import interfaceadapter.history.TransactionHistoryPresenter;
import usecase.transaction_history.TransactionHistoryInputBoundary;
import usecase.transaction_history.TransactionHistoryInteractor;
import usecase.transaction_history.TransactionHistoryOutputBoundary;

/**
 * Wires transaction history use case.
 */
public class HistoryUseCaseConfigurator {

    private final TransactionDataAccessInterface transactionDataAccessObject;
    private final ViewConfigurator views;

    public HistoryUseCaseConfigurator(
            final TransactionDataAccessInterface transactionDataAccessObject,
            final ViewConfigurator views
    ) {
        this.transactionDataAccessObject = transactionDataAccessObject;
        this.views = views;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        TransactionHistoryOutputBoundary presenter =
                new TransactionHistoryPresenter(
                        views.getHistoryViews().getHistoryViewModel()
                );

        TransactionHistoryInputBoundary interactor =
                new TransactionHistoryInteractor(
                        transactionDataAccessObject,
                        presenter,
                        views.getLoggedInViews().getLoggedInViewModel()
                );

        TransactionHistoryController controller =
                new TransactionHistoryController(interactor);
        views.getHistoryViews().getHistoryView().setTransactionHistoryController(controller);
    }
}
