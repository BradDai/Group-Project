package interfaceadapter.logged_in;

import interfaceadapter.ViewModel;

/**
 * The View Model for the Logged In View.
 */
public class LoggedInViewModel extends ViewModel<LoggedInState> {

    public static final String SUBACCOUNTS_CHANGED = "subAccounts";
    public static final String SUBACCOUNT_ERROR = "subAccountError";

    public LoggedInViewModel() {
        super("logged in");
        setState(new LoggedInState());
    }

}
