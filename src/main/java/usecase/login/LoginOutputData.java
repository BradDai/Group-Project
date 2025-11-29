package usecase.login;

import java.util.Collections;
import java.util.List;

import entity.SubAccount;

/**
 * Output Data for the Login Use Case.
 * @param username .
 * @param subAccounts .
 */
public record LoginOutputData(String username, List<SubAccount> subAccounts) {

    public LoginOutputData(final String username, final List<SubAccount> subAccounts) {
        this.username = username;
        this.subAccounts = subAccounts == null
            ? Collections.emptyList()
            : List.copyOf(subAccounts);
    }
}
