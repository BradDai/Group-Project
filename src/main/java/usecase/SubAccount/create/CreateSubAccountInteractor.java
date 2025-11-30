package usecase.SubAccount.create;

import java.math.BigDecimal;
import java.util.List;

import entity.SubAccount;
import usecase.SubAccount.SubAccountDataAccessInterface;

/**
 * CreateSubAccount Interactor.
 */
public class CreateSubAccountInteractor implements CreateSubAccountInputBoundary {

    private static final int MAX_SUBACCOUNTS = 5;
    private final SubAccountDataAccessInterface subAccountDataAccess;
    private final CreateSubAccountOutputBoundary presenter;

    public CreateSubAccountInteractor(final SubAccountDataAccessInterface subAccountDataAccess,
                                      final CreateSubAccountOutputBoundary presenter) {
        this.subAccountDataAccess = subAccountDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(final CreateSubAccountInputData inputData) {

        final String username = inputData.username();
        final String newName = inputData.subAccountName();
        if (newName == null || newName.isBlank()) {
            presenter.prepareFailView("Subaccount name cannot be empty.");
            return;
        }

        if (subAccountDataAccess.countByUser(username) >= MAX_SUBACCOUNTS) {
            presenter.prepareFailView("Maximum subaccount limit reached (5).");
            return;
        }

        if (subAccountDataAccess.exists(username, newName)) {
            presenter.prepareFailView("Subaccount with this name already exists.");
            return;
        }
        final SubAccount newSa =
            new SubAccount(newName.trim(), BigDecimal.ZERO, false);
        subAccountDataAccess.save(username, newSa);
        final List<SubAccount> all = subAccountDataAccess.getSubAccountsOf(username);
        final CreateSubAccountOutputData outputData =
            new CreateSubAccountOutputData(username, all);
        presenter.prepareSuccessView(outputData);
    }
}
