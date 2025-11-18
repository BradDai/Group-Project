package use_case.transfer;

/**
 * Input Boundary for the Transfer Use Case.
 */
public interface TransferInputBoundary {
    /**
     * Executes the transfer use case.
     * @param transferInputData the input data
     */
    void execute(TransferInputData transferInputData);
}