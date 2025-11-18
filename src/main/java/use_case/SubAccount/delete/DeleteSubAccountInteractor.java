package use_case.SubAccount.delete;
import entity.SubAccount;
import use_case.SubAccount.SubAccountDataAccessInterface;
import java.util.List;
public class DeleteSubAccountInteractor implements DeleteSubAccountInputBoundary{
    private final SubAccountDataAccessInterface subAccountDataAccess;
    private final DeleteSubAccountOutputBoundary presenter;
    public DeleteSubAccountInteractor(SubAccountDataAccessInterface subAccountDataAccess,
                                      DeleteSubAccountOutputBoundary presenter) {
        this.subAccountDataAccess = subAccountDataAccess;
        this.presenter = presenter;
    }
    @Override
    public void execute(DeleteSubAccountInputData inputData) {
        String username = inputData.getUsername();
        String name = inputData.getSubAccountName();
        if (name == null || name.isBlank()) {
            presenter.prepareFailView("Subaccount name cannot be empty.");
            return;
        }
        if (!subAccountDataAccess.exists(username, name)) {
            presenter.prepareFailView("Subaccount not found.");
            return;
        }
        List<SubAccount> current = subAccountDataAccess.getSubAccountsOf(username);
        SubAccount target = current.stream()
                .filter(sa -> sa.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        if (target == null) {
            presenter.prepareFailView("Subaccount not found.");
            return;
        }
        if (target.isUndeletable()) {
            presenter.prepareFailView("This subaccount cannot be deleted.");
            return;
        }
        subAccountDataAccess.delete(username, name);
        List<SubAccount> updated = subAccountDataAccess.getSubAccountsOf(username);
        DeleteSubAccountOutputData outputData =
                new DeleteSubAccountOutputData(username, updated);
        presenter.prepareSuccessView(outputData);
    }

}
