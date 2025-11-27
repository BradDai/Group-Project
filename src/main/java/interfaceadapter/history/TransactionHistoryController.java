package interfaceadapter.history;

import usecase.transaction_history.TransactionHistoryInputBoundary;
import usecase.transaction_history.TransactionHistoryInputData;

public class TransactionHistoryController {

    private final TransactionHistoryInputBoundary interactor;

    public TransactionHistoryController(final TransactionHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    // This name and signature must match the call in HistoryView
    public void loadHistory(final String portfolio,
                            final String asset,
                            final String startDate,
                            final String endDate) {

        System.out.println("[Controller] loadHistory called with:");
        System.out.println("  portfolio = " + portfolio);
        System.out.println("  asset     = " + asset);
        System.out.println("  startDate = " + startDate);
        System.out.println("  endDate   = " + endDate);

        final TransactionHistoryInputData input =
            new TransactionHistoryInputData(portfolio, asset, startDate, endDate);

        interactor.execute(input);
    }
}






