package usecase.SubAccount.delete;

import java.math.BigDecimal;
import java.util.List;

import entity.SubAccount;
import usecase.SubAccount.SubAccountDataAccessInterface;

public class DeleteSubAccountInteractor implements DeleteSubAccountInputBoundary {
    private final SubAccountDataAccessInterface subAccountDataAccess;
    private final DeleteSubAccountOutputBoundary presenter;

    public DeleteSubAccountInteractor(final SubAccountDataAccessInterface subAccountDataAccess,
                                      final DeleteSubAccountOutputBoundary presenter) {
        this.subAccountDataAccess = subAccountDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(final DeleteSubAccountInputData inputData) {
        final String username = inputData.username();
        final String name = inputData.subAccountName();
        if (name == null || name.isBlank()) {
            presenter.prepareFailView("Subaccount name cannot be empty.");
            return;
        }
        if (!subAccountDataAccess.exists(username, name)) {
            presenter.prepareFailView("Subaccount not found.");
            return;
        }
        final List<SubAccount> current = subAccountDataAccess.getSubAccountsOf(username);
        final SubAccount target = current.stream()
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
        if (target.getBalanceUSD().compareTo(BigDecimal.ZERO) != 0) {
            presenter.prepareFailView("Can't delete a subaccount with non-zero balance.");
            return;
        }
        subAccountDataAccess.delete(username, name);
        final List<SubAccount> updated = subAccountDataAccess.getSubAccountsOf(username);
        final DeleteSubAccountOutputData outputData =
            new DeleteSubAccountOutputData(username, updated);
        presenter.prepareSuccessView(outputData);
    }

}
