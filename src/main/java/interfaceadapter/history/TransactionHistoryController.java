package interfaceadapter.history;

import usecase.transaction_history.TransactionHistoryInputBoundary;
import usecase.transaction_history.TransactionHistoryInputData;

public class TransactionHistoryController {

    private final TransactionHistoryInputBoundary interactor;

    public TransactionHistoryController(final TransactionHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * L.
     * @param portfolio .
     * @param asset .
     * @param startDate .
     * @param endDate .
     */
    public void loadHistory(final String portfolio,
                            final String asset,
                            final String startDate,
                            final String endDate) {

        final TransactionHistoryInputData input =
                new TransactionHistoryInputData(portfolio, asset, startDate, endDate);

        interactor.execute(input);
    }

    /**
     * L.
     *
     */
    public void loadPortfolioOptions() {
        interactor.loadPortfolioOptions();
    }
}





