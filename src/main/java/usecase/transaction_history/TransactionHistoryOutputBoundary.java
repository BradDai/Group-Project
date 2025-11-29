package usecase.transaction_history;

import java.util.ArrayList;

public interface TransactionHistoryOutputBoundary {
    void present(TransactionHistoryOutputData outputData);
    void presentPortfolioOptions(ArrayList<String> portfolioIds);
//
}
