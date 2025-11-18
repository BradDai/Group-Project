package use_case.SubAccount.delete;

public interface DeleteSubAccountOutputBoundary {
    void prepareSuccessView(DeleteSubAccountOutputData outputData);
    void prepareFailView(String errorMessage);
}