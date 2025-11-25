package interface_adapter;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.SubAccount.SubAccountDataAccessInterface;
import use_case.switch_loggedin.SwitchLoggedInOutputBoundary;

public class SwitchLoggedInPresenter implements SwitchLoggedInOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;
    private final SubAccountDataAccessInterface subAccountDataAccess;

    public SwitchLoggedInPresenter(final LoggedInViewModel loggedInViewModel,
                                   final ViewManagerModel viewManagerModel,
                                   final SubAccountDataAccessInterface subAccountDataAccess) {
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.subAccountDataAccess = subAccountDataAccess;
    }

    public void switchToLoggedInView() {
        // Refresh the subaccounts before switching
        final LoggedInState state = loggedInViewModel.getState();
        final String username = state.getUsername();

        if (username != null && !username.isEmpty()) {
            state.setSubAccounts(subAccountDataAccess.getSubAccountsOf(username));
            loggedInViewModel.setState(state);
            loggedInViewModel.firePropertyChange();
        }

        viewManagerModel.setState(loggedInViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}