package interfaceadapter.subaccount.delete;

import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.SubAccount.delete.DeleteSubAccountOutputBoundary;
import usecase.SubAccount.delete.DeleteSubAccountOutputData;

public class DeleteSubAccountPresenter implements DeleteSubAccountOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;

    public DeleteSubAccountPresenter(final LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(final DeleteSubAccountOutputData outputData) {
        final LoggedInState state = loggedInViewModel.getState();
        state.setSubAccounts(outputData.subAccounts());
        state.setSubAccountError(null);
        loggedInViewModel.firePropertyChange("subAccounts");
    }

    @Override
    public void prepareFailView(final String errorMessage) {
        final LoggedInState state = loggedInViewModel.getState();
        state.setSubAccountError(errorMessage);
        loggedInViewModel.firePropertyChange("subAccountError");
    }
}
