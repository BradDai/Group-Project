package interface_adapter.subaccount.delete;
import use_case.SubAccount.delete.DeleteSubAccountInputBoundary;
import use_case.SubAccount.delete.DeleteSubAccountInputData;
public class DeleteSubAccountController {
    private final DeleteSubAccountInputBoundary interactor;
    public DeleteSubAccountController(DeleteSubAccountInputBoundary interactor) {
        this.interactor = interactor;
    }
    public void execute(String username, String subName) {
        DeleteSubAccountInputData inputData = new DeleteSubAccountInputData(username, subName);
        interactor.execute(inputData);
    }
}
