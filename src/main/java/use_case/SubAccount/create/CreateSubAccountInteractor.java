package use_case.SubAccount.create;

import entity.SubAccount;
import use_case.SubAccount.SubAccountDataAccessInterface;

import java.math.BigDecimal;
import java.util.List;

/**
 * CreateSubAccount Interactor.
 */
public class CreateSubAccountInteractor implements CreateSubAccountInputBoundary {

    private final SubAccountDataAccessInterface subAccountDataAccess;
    private final CreateSubAccountOutputBoundary presenter;

    private static final int MAX_SUBACCOUNTS = 5;

    public CreateSubAccountInteractor(SubAccountDataAccessInterface subAccountDataAccess,
                                      CreateSubAccountOutputBoundary presenter) {
        this.subAccountDataAccess = subAccountDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(CreateSubAccountInputData inputData) {

        String username = inputData.getUsername();
        String newName = inputData.getSubAccountName();
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
        SubAccount newSA =
                new SubAccount(newName.trim(), BigDecimal.ZERO, false);
        subAccountDataAccess.save(username, newSA);
        List<SubAccount> all = subAccountDataAccess.getSubAccountsOf(username);
        CreateSubAccountOutputData outputData =
                new CreateSubAccountOutputData(username, all);
        presenter.prepareSuccessView(outputData);
    }
}