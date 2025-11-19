package interface_adapter.transfer;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.transfer.TransferOutputBoundary;
import use_case.transfer.TransferOutputData;

public class TransferPresenter implements TransferOutputBoundary {
    private final TransferViewModel transferViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    public TransferPresenter(TransferViewModel transferViewModel,
                             LoggedInViewModel loggedInViewModel,
                             ViewManagerModel viewManagerModel) {
        this.transferViewModel = transferViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(TransferOutputData outputData) {
        TransferState state = transferViewModel.getState();
        state.setError(null);
        transferViewModel.setState(state);
        transferViewModel.firePropertyChanged();

        viewManagerModel.setState(loggedInViewModel.getViewName());
        viewManagerModel.firePropertyChange();

        String message = "Transfer successful!";
        loggedInViewModel.firePropertyChange("notification", null, message);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        TransferState state = transferViewModel.getState();
        state.setError(errorMessage);
        transferViewModel.setState(state);
        transferViewModel.firePropertyChanged("error");
    }

    @Override
    public void presentBalances(double fromBalance, double toBalance) {
        TransferState state = transferViewModel.getState();
        state.setFromBalance(String.format("%.2f", fromBalance));
        state.setToBalance(String.format("%.2f", toBalance));

        transferViewModel.setState(state);
        transferViewModel.firePropertyChanged();
    }
}