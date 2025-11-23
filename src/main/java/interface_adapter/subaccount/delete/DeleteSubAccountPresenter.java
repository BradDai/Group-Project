package interface_adapter.subaccount.delete;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.SubAccount.delete.DeleteSubAccountOutputBoundary;
import use_case.SubAccount.delete.DeleteSubAccountOutputData;

public class DeleteSubAccountPresenter implements DeleteSubAccountOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;

    public DeleteSubAccountPresenter(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(DeleteSubAccountOutputData outputData) {
        LoggedInState state = loggedInViewModel.getState();
        state.setSubAccounts(outputData.getSubAccounts());
        state.setSubAccountError(null);
        loggedInViewModel.firePropertyChange("subAccounts");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        LoggedInState state = loggedInViewModel.getState();
        state.setSubAccountError(errorMessage);
        loggedInViewModel.firePropertyChange("subAccountError");
    }
}