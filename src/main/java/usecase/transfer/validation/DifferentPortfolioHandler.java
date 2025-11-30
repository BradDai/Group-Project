package usecase.transfer.validation;

import usecase.transfer.TransferDataAccessInterface;
import usecase.transfer.TransferInputData;
import usecase.transfer.TransferOutputBoundary;

public class DifferentPortfolioHandler extends AbstractTransferValidationHandler {
    @Override
    protected boolean check(TransferInputData data, TransferDataAccessInterface dataAccess,
                            TransferOutputBoundary presenter) {
        boolean flag = true;
        if (data.fromPortfolio().equals(data.toPortfolio())) {
            presenter.prepareFailView("Cannot transfer to the same portfolio");
            flag = false;
        }
        return flag;
    }
}
