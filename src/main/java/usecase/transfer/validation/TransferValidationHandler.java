package usecase.transfer.validation;

import usecase.transfer.TransferDataAccessInterface;
import usecase.transfer.TransferInputData;
import usecase.transfer.TransferOutputBoundary;

public interface TransferValidationHandler {
    /**
     * Set the next handler.
     *
     * @param next next handler
     */
    void setNext(TransferValidationHandler next);

    /**
     * Check if the condition is valid.
     * @param data input data
     * @param dataAccess data access interface
     * @param presenter output data boundary
     * @return TRUE if valid
     */
    boolean validate(TransferInputData data, TransferDataAccessInterface dataAccess, TransferOutputBoundary presenter);
}
