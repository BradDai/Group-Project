package use_case.SubAccount.create;

/**
 * Input data for Create SubAccount Use Case.
 */
public class CreateSubAccountInputData {

    private final String username;
    private final String subAccountName;

    public CreateSubAccountInputData(final String username, final String subAccountName) {
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
