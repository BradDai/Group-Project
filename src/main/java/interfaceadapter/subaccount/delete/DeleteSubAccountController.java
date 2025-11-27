package interfaceadapter.subaccount.delete;

import usecase.SubAccount.delete.DeleteSubAccountInputBoundary;
import usecase.SubAccount.delete.DeleteSubAccountInputData;

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
