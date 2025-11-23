/*package interface_adapter.transaction_history;

import use_case.transaction_history.TransactionHistoryInputBoundary;
import use_case.transaction_history.TransactionHistoryInputData;

public class TransactionHistoryController {

    private final TransactionHistoryInputBoundary interactor;

    public TransactionHistoryController(TransactionHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void loadHistory(String portfolio, String assetFilter) {
        TransactionHistoryInputData input =
                new TransactionHistoryInputData(portfolio, assetFilter);
        interactor.execute(input); //
    }
}*/

package interface_adapter.history;

import use_case.transaction_history.TransactionHistoryInputBoundary;
import use_case.transaction_history.TransactionHistoryInputData;

public class TransactionHistoryController {

    private final TransactionHistoryInputBoundary interactor;

    public TransactionHistoryController(TransactionHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void loadHistory(String portfolio,
                            String assetFilter,
                            String startDate,
                            String endDate) {

        TransactionHistoryInputData input =
                new TransactionHistoryInputData(portfolio, assetFilter, startDate, endDate);
        interactor.execute(input);
    }
}


