package interface_adapter.transfer;

import use_case.transfer.TransferOutputBoundary;
import use_case.transfer.TransferOutputData;

/**
 * Presenter for the Transfer Use Case.
 */
public class TransferPresenter implements TransferOutputBoundary {
    private final TransferViewModel transferViewModel;

    public TransferPresenter(TransferViewModel transferViewModel) {
        this.transferViewModel = transferViewModel;
    }

    @Override
    public void prepareSuccessView(TransferOutputData outputData) {
        // Transfer was successful
        // The view will show the success message and navigate back
        final TransferState transferState = transferViewModel.getState();
        transferState.setError("");
        transferViewModel.setState(transferState);
        transferViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final TransferState transferState = transferViewModel.getState();
        transferState.setError(errorMessage);
        transferViewModel.setState(transferState);
        transferViewModel.firePropertyChanged("error");
    }
}