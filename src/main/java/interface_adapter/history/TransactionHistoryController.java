

package interface_adapter.history;

import use_case.transaction_history.TransactionHistoryInputBoundary;
import use_case.transaction_history.TransactionHistoryInputData;

public class TransactionHistoryController {

    private final TransactionHistoryInputBoundary interactor;

    public TransactionHistoryController(TransactionHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    // This name and signature must match the call in HistoryView
    public void loadHistory(String portfolio,
                            String asset,
                            String startDate,
                            String endDate) {

        System.out.println("[Controller] loadHistory called with:");
        System.out.println("  portfolio = " + portfolio);
        System.out.println("  asset     = " + asset);
        System.out.println("  startDate = " + startDate);
        System.out.println("  endDate   = " + endDate);

        TransactionHistoryInputData input =
                new TransactionHistoryInputData(portfolio, asset, startDate, endDate);

        interactor.execute(input);
    }
}






