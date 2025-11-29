package interfaceadapter.logged_in;

import java.util.ArrayList;
import java.util.List;

import entity.SubAccount;

/**
 * The State information representing the logged-in user.
 */
public class LoggedInState {
    private String username = "";
    private String password = "";
    private String passwordError;
    private List<SubAccount> subAccounts = new ArrayList<>();
    private String subAccountError;

    public LoggedInState(final LoggedInState copy) {
        username = copy.username;
        password = copy.password;
        passwordError = copy.passwordError;

        subAccountError = copy.subAccountError;

        if (copy.subAccounts != null) {
            this.subAccounts = new ArrayList<>(copy.subAccounts);
        }
        else {
            this.subAccounts = new ArrayList<>();
        }
    }

    private String currentPortfolioName = "";

    public LoggedInState() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPasswordError(final String passwordError) {
        this.passwordError = passwordError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public List<SubAccount> getSubAccounts() {
        return subAccounts;
    }

    public void setSubAccounts(final List<SubAccount> subAccounts) {
        this.subAccounts = subAccounts;
    }

    public String getSubAccountError() {
        return subAccountError;
    }

    public void setSubAccountError(final String subAccountError) {
        this.subAccountError = subAccountError;
    }

    public String getCurrentPortfolioName() {
        return currentPortfolioName;
    }

    public void setCurrentPortfolioName(final String currentPortfolioName) {
        this.currentPortfolioName = currentPortfolioName;
    }

}
