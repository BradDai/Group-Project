package use_case.SubAccount.create;

public interface CreateSubAccountOutputBoundary {

    void prepareSuccessView(CreateSubAccountOutputData outputData);

    void prepareFailView(String errorMessage);
}