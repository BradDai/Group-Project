package interface_adapter.subaccount.delete;

import use_case.SubAccount.delete.DeleteSubAccountInputBoundary;
import use_case.SubAccount.delete.DeleteSubAccountInputData;

public class DeleteSubAccountController {
    private final DeleteSubAccountInputBoundary interactor;

    public DeleteSubAccountController(final DeleteSubAccountInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(final String username, final String subName) {
        final DeleteSubAccountInputData inputData = new DeleteSubAccountInputData(username, subName);
        interactor.execute(inputData);
    }
}
