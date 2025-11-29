package interfaceadapter.history;

import java.util.ArrayList;

import usecase.transaction_history.TransactionHistoryOutputBoundary;
import usecase.transaction_history.TransactionHistoryOutputData;

public class TransactionHistoryPresenter implements TransactionHistoryOutputBoundary {

    private final HistoryViewModel historyViewModel;

    public TransactionHistoryPresenter(final HistoryViewModel historyViewModel) {
        this.historyViewModel = historyViewModel;
    }

    @Override
    public void present(final TransactionHistoryOutputData outputData) {

        final HistoryState state = historyViewModel.getState();
        state.setRows(outputData.rows());
        state.setMessage(outputData.message());

        historyViewModel.setState(state);
        historyViewModel.firePropertyChanged();
    }

    @Override
    public void presentPortfolioOptions(final ArrayList<String> portfolioNames) {

        final HistoryState state = historyViewModel.getState();
        state.setPortfolioOptions(portfolioNames);

        historyViewModel.setState(state);
        historyViewModel.firePropertyChanged();
    }
}
