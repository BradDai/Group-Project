package use_case.login;

import entity.SubAccount;

import java.util.Collections;
import java.util.List;

/**
 * Output Data for the Login Use Case.
 */
public class LoginOutputData {

    private final String username;
    private final List<SubAccount> subAccounts;

    public LoginOutputData(String username) {
        this(username, Collections.emptyList());
    }

    public LoginOutputData(String username, List<SubAccount> subAccounts) {
        this.username = username;
        this.subAccounts = subAccounts == null
                ? Collections.emptyList()
                : List.copyOf(subAccounts);
    }

    public String getUsername() {
        return username;
    }

    public List<SubAccount> getSubAccounts() {
        return subAccounts;
    }
}