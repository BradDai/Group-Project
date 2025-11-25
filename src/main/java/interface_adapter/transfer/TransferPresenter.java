package interface_adapter.transfer;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.transfer.TransferOutputBoundary;
import use_case.transfer.TransferOutputData;

public class TransferPresenter implements TransferOutputBoundary {
    private final TransferViewModel transferViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    public TransferPresenter(final TransferViewModel transferViewModel,
                             final LoggedInViewModel loggedInViewModel,
                             final ViewManagerModel viewManagerModel) {
        this.transferViewModel = transferViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(final TransferOutputData outputData) {
        final TransferState state = transferViewModel.getState();
        state.setError(null);
        state.setAmount("");

        transferViewModel.setState(state);
        transferViewModel.firePropertyChanged();

        final LoggedInState loggedInState = loggedInViewModel.getState();
        loggedInState.setSubAccounts(outputData.getUpdatedAccounts());
        loggedInViewModel.setState(loggedInState);
        loggedInViewModel.firePropertyChange();

        viewManagerModel.setState(loggedInViewModel.getViewName());
        viewManagerModel.firePropertyChange();

        final String message = "Transfer successful!";
        loggedInViewModel.firePropertyChange("notification", null, message);
    }

    @Override
    public void prepareFailView(final String errorMessage) {
        final TransferState state = transferViewModel.getState();
        state.setError(errorMessage);
        transferViewModel.setState(state);
        transferViewModel.firePropertyChanged("error");
    }

    @Override
    public void presentBalances(final double fromBalance, final double toBalance, final String[] availableCurrencies) {
        final TransferState state = transferViewModel.getState();
        state.setFromBalance(String.format("%.2f", fromBalance));
        state.setToBalance(String.format("%.2f", toBalance));
        state.setAvailableCurrencies(availableCurrencies);

        transferViewModel.setState(state);
        transferViewModel.firePropertyChanged(); // This triggers the View
    }
}
