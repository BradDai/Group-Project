package interface_adapter.logged_in;

import entity.SubAccount;
import java.util.ArrayList;
import java.util.List;

/**
 * The State information representing the logged-in user.
 */
public class LoggedInState {
    private String username = "";
    private String password = "";
    private String passwordError;
    private List<SubAccount> subAccounts = new ArrayList<>();
    private String subAccountError;

    public LoggedInState(LoggedInState copy) {
        username = copy.username;
        password = copy.password;
        passwordError = copy.passwordError;

        // --- FIX: Copy these missing fields ---
        subAccountError = copy.subAccountError;

        // Create a new list containing the same elements (shallow copy of list)
        if (copy.subAccounts != null) {
            this.subAccounts = new ArrayList<>(copy.subAccounts);
        } else {
            this.subAccounts = new ArrayList<>();
        }
    }

    // Because of the previous copy constructor, the default constructor must be explicit.
    public LoggedInState() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public List<SubAccount> getSubAccounts() {
        return subAccounts;
    }

    public void setSubAccounts(List<SubAccount> subAccounts) {
        this.subAccounts = subAccounts;
    }

    public String getSubAccountError() {
        return subAccountError;
    }

    public void setSubAccountError(String subAccountError) {
        this.subAccountError = subAccountError;
    }
}