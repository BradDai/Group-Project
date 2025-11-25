package use_case.SubAccount.delete;

public class DeleteSubAccountInputData {
    private final String username;
    private final String subAccountName;

    public DeleteSubAccountInputData(final String username, final String subAccountName) {
        this.username = username;
        this.subAccountName = subAccountName;
    }

    public String getUsername() {
        return username;
    }

    public String getSubAccountName() {
        return subAccountName;
    }
}
