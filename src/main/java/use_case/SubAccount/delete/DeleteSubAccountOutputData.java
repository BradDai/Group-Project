package use_case.SubAccount.delete;

import entity.SubAccount;
import java.util.List;

public class DeleteSubAccountOutputData {
    private final String username;
    private final List<SubAccount> subAccounts;
    public DeleteSubAccountOutputData(String username, List<SubAccount> subAccounts) {
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