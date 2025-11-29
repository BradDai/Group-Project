package usecase.transaction_history;

public interface TransactionHistoryInputBoundary {
    /**
     * T.
     * @param inputData .
     */
    void execute(TransactionHistoryInputData inputData);

    /**
     * T.
     */
    void loadPortfolioOptions();
}
