package use_case.SubAccount.delete;

import java.util.List;

import entity.SubAccount;

public class DeleteSubAccountOutputData {
    private final String username;
    private final List<SubAccount> subAccounts;

    public DeleteSubAccountOutputData(final String username, final List<SubAccount> subAccounts) {
        this.username = username;
        this.subAccounts = subAccounts;
    }

    public String getUsername() {
        return username;
    }

    public List<SubAccount> getSubAccounts() {
        return subAccounts;
    }
}
