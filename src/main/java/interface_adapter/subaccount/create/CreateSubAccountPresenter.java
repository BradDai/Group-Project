package interface_adapter.subaccount.create;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.SubAccount.create.CreateSubAccountOutputBoundary;
import use_case.SubAccount.create.CreateSubAccountOutputData;

public class CreateSubAccountPresenter implements CreateSubAccountOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;

    public CreateSubAccountPresenter(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(CreateSubAccountOutputData outputData) {
        LoggedInState state = loggedInViewModel.getState();

        state.setUsername(outputData.getUsername());
        state.setSubAccounts(outputData.getAllSubAccounts());

        loggedInViewModel.setState(state);
        loggedInViewModel.firePropertyChange("subAccounts");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        LoggedInState state = loggedInViewModel.getState();
        state.setSubAccountError(errorMessage);
        loggedInViewModel.setState(state);
        loggedInViewModel.firePropertyChange("subAccountError");
    }
}