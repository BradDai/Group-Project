package interfaceadapter.subaccount.create;

import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.SubAccount.create.CreateSubAccountOutputBoundary;
import usecase.SubAccount.create.CreateSubAccountOutputData;

public class CreateSubAccountPresenter implements CreateSubAccountOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;

    public CreateSubAccountPresenter(final LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(final CreateSubAccountOutputData outputData) {
        final LoggedInState state = loggedInViewModel.getState();

        state.setUsername(outputData.username());
        state.setSubAccounts(outputData.allSubAccounts());

        loggedInViewModel.setState(state);
        loggedInViewModel.firePropertyChange("subAccounts");
    }

    @Override
    public void prepareFailView(final String errorMessage) {
        final LoggedInState state = loggedInViewModel.getState();
        state.setSubAccountError(errorMessage);
        loggedInViewModel.setState(state);
        loggedInViewModel.firePropertyChange("subAccountError");
    }
}
