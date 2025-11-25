package interface_adapter.subaccount.create;

import use_case.SubAccount.create.CreateSubAccountInputBoundary;
import use_case.SubAccount.create.CreateSubAccountInputData;

public class CreateSubAccountController {

    private final CreateSubAccountInputBoundary interactor;

    public CreateSubAccountController(final CreateSubAccountInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(final String username, final String newSubAccountName) {
        final CreateSubAccountInputData data =
            new CreateSubAccountInputData(username, newSubAccountName);
        interactor.execute(data);
    }
}
