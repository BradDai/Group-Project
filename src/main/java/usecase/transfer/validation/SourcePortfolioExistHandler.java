package usecase.transfer.validation;

import usecase.transfer.TransferDataAccessInterface;
import usecase.transfer.TransferInputData;
import usecase.transfer.TransferOutputBoundary;

public class SourcePortfolioExistHandler extends AbstractTransferValidationHandler {
    @Override
    protected boolean check(TransferInputData data, TransferDataAccessInterface dataAccess,
                            TransferOutputBoundary presenter) {
        boolean flag = true;
        if (!dataAccess.portfolioExists(data.username(), data.fromPortfolio())) {
            presenter.prepareFailView("Source portfolio does not exist: " + data.fromPortfolio());
            flag = false;
        }
        return flag;
    }
}
