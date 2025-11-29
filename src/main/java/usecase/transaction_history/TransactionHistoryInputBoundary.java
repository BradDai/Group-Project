package usecase.transaction_history;

public interface TransactionHistoryInputBoundary {
    void execute(TransactionHistoryInputData inputData);
    void loadPortfolioOptions();
}
//
