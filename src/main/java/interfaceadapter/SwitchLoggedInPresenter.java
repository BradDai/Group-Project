package interfaceadapter;

import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.SubAccount.SubAccountDataAccessInterface;
import usecase.switch_loggedin.SwitchLoggedInOutputBoundary;

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

    /**
     * T.
     */
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
