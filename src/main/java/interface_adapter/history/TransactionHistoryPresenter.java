//package interface_adapter.transaction_history;
//
//import interface_adapter.history.HistoryState;
//import interface_adapter.history.HistoryViewModel;
//import use_case.transaction_history.TransactionHistoryOutputBoundary;
//import use_case.transaction_history.TransactionHistoryOutputData;
//
//public class TransactionHistoryPresenter implements TransactionHistoryOutputBoundary {
//
//    private final HistoryViewModel viewModel;
//
//    public TransactionHistoryPresenter(HistoryViewModel viewModel) {
//        this.viewModel = viewModel;
//    }
//
//    @Override
//    public void present(TransactionHistoryOutputData outputData) {
//        HistoryState state = viewModel.getState();
//        state.setRows(outputData.getRows());
//        state.setMessage(outputData.getMessage());
//        viewModel.setState(state); //
//    }
//}
package interface_adapter.history;

import use_case.transaction_history.TransactionHistoryOutputBoundary;
import use_case.transaction_history.TransactionHistoryOutputData;

public class TransactionHistoryPresenter implements TransactionHistoryOutputBoundary {

    private final HistoryViewModel viewModel;

    public TransactionHistoryPresenter(HistoryViewModel viewModel) {  // <== must match AppBuilder
        this.viewModel = viewModel;
    }

    @Override
    public void present(TransactionHistoryOutputData outputData) {
        HistoryState state = viewModel.getState();
        state.setRows(outputData.getRows());
        state.setMessage(outputData.getMessage());
        viewModel.setState(state);       // triggers property change in VM

    }
}

