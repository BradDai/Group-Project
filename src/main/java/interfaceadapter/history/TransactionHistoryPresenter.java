package interfaceadapter.history;

import usecase.transaction_history.TransactionHistoryOutputBoundary;
import usecase.transaction_history.TransactionHistoryOutputData;

import java.util.ArrayList;

public class TransactionHistoryPresenter implements TransactionHistoryOutputBoundary {

    private final HistoryViewModel historyViewModel;

    public TransactionHistoryPresenter(final HistoryViewModel historyViewModel) {
        this.historyViewModel = historyViewModel;
    }

    @Override
    public void present(final TransactionHistoryOutputData outputData) {

        HistoryState state = historyViewModel.getState();
        state.setRows(outputData.rows());
        state.setMessage(outputData.message());

        historyViewModel.setState(state);
        historyViewModel.firePropertyChanged();
    }

    @Override
    public void presentPortfolioOptions(final ArrayList<String> portfolioNames) {


        HistoryState state = historyViewModel.getState();
        state.setPortfolioOptions(portfolioNames);

        historyViewModel.setState(state);
        historyViewModel.firePropertyChanged();
    }
}




