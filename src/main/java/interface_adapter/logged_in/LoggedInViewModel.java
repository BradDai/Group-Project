package interface_adapter.logged_in;

import interface_adapter.ViewModel;

/**
 * The View Model for the Logged In View.
 */
public class LoggedInViewModel extends ViewModel<LoggedInState> {

    public LoggedInViewModel() {
        super("logged in");
        setState(new LoggedInState());
    }
    public static final String SUBACCOUNTS_CHANGED = "subAccounts";
    public static final String SUBACCOUNT_ERROR = "subAccountError";

}
