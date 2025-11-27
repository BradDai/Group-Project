package usecase.SubAccount.delete;

public interface DeleteSubAccountOutputBoundary {
    void prepareSuccessView(DeleteSubAccountOutputData outputData);

    void prepareFailView(String errorMessage);
}
