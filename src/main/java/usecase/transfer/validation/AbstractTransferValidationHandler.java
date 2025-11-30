package usecase.transfer.validation;

import usecase.transfer.TransferDataAccessInterface;
import usecase.transfer.TransferInputData;
import usecase.transfer.TransferOutputBoundary;

public abstract class AbstractTransferValidationHandler implements TransferValidationHandler {
    private TransferValidationHandler next;

    @Override
    public void setNext(TransferValidationHandler handler) {
        this.next = handler;
    }

    @Override
    public boolean validate(TransferInputData data, TransferDataAccessInterface dataAccess,
                            TransferOutputBoundary presenter) {
        if (!check(data, dataAccess, presenter)) {
            return false;
        }

        if (next != null) {
            return next.validate(data, dataAccess, presenter);
        }

        return true;
    }

    protected abstract boolean check(TransferInputData data, TransferDataAccessInterface dataAccess,
                                     TransferOutputBoundary presenter);
}
