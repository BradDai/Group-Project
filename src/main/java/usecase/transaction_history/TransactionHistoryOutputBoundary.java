package usecase.transaction_history;

import java.util.ArrayList;

public interface TransactionHistoryOutputBoundary {
    /**
     *  T.
     * @param outputData .
     */
    void present(TransactionHistoryOutputData outputData);

    /**
     * T.
     * @param portfolioIds .
     */
    void presentPortfolioOptions(ArrayList<String> portfolioIds);
}
