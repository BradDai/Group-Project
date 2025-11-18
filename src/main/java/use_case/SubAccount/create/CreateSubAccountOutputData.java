package use_case.SubAccount.create;

import entity.SubAccount;

import java.util.List;

/**
 * Output data that Presenter will use to update ViewModel.
 */
public class CreateSubAccountOutputData {

    private final String username;
    private final List<SubAccount> allSubAccounts;

    public CreateSubAccountOutputData(String username, List<SubAccount> allSubAccounts) {
        this.username = username;
        this.allSubAccounts = allSubAccounts;
    }

    public String getUsername() {
        return username;
    }

    public List<SubAccount> getAllSubAccounts() {
        return allSubAccounts;
    }
}