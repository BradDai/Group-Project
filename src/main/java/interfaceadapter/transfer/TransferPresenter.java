package interfaceadapter.transfer;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.transfer.TransferOutputBoundary;
import usecase.transfer.TransferOutputData;

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
        loggedInState.setSubAccounts(outputData.updatedAccounts());
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
    public void presentBalances(final double fromBalance, final double toBalance, final String[] currencyList,
                                final String[] stockList) {
        final TransferState state = transferViewModel.getState();
        state.setFromBalance(String.format("%.2f", fromBalance));
        state.setToBalance(String.format("%.2f", toBalance));

        state.setAvailableCurrencies(currencyList);
        state.setAvailableStocks(stockList);

        transferViewModel.setState(state);
        transferViewModel.firePropertyChanged();
    }
}
