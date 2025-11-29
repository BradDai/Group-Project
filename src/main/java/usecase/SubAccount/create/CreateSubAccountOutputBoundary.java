package usecase.SubAccount.create;

public interface CreateSubAccountOutputBoundary {

    /**
     * Y.
     * @param outputData .
     */
    void prepareSuccessView(CreateSubAccountOutputData outputData);

    /**
     * Y.
     * @param errorMessage .
     */
    void prepareFailView(String errorMessage);
}
