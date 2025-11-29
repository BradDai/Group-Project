package usecase.SubAccount.delete;

public interface DeleteSubAccountOutputBoundary {
    /**
     * T.
     * @param outputData .
     */
    void prepareSuccessView(DeleteSubAccountOutputData outputData);

    /**
     * Y.
     * @param errorMessage .
     */
    void prepareFailView(String errorMessage);
}
