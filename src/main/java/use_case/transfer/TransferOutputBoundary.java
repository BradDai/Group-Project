package use_case.transfer;

/**
 * Output Boundary for the Transfer Use Case.
 */
public interface TransferOutputBoundary {
    /**
     * Prepares the success view for the Transfer Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(TransferOutputData outputData);

    /**
     * Prepares the failure view for the Transfer Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}