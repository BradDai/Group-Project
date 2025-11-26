package interfaceadapter.history;

import usecase.transaction_history.TransactionHistoryOutputBoundary;
import usecase.transaction_history.TransactionHistoryOutputData;

public class TransactionHistoryPresenter implements TransactionHistoryOutputBoundary {

    private final HistoryViewModel historyViewModel;

    public TransactionHistoryPresenter(final HistoryViewModel historyViewModel) {
        this.historyViewModel = historyViewModel;
    }

    @Override
    public void present(final TransactionHistoryOutputData outputData) {
        System.out.println("[Presenter] present() called");
        System.out.println("[Presenter] rows size = " + outputData.rows().size());
        System.out.println("[Presenter] message   = " + outputData.message());

        // Get current state and update it
        final HistoryState state = historyViewModel.getState();
        state.setRows(outputData.rows());
        state.setMessage(outputData.message());

        // IMPORTANT: update VM and fire change
        historyViewModel.setState(state);          // stores new state
        historyViewModel.firePropertyChanged();    // notifies HistoryView

        System.out.println("[Presenter] ViewModel updated & property change fired");
    }
}



