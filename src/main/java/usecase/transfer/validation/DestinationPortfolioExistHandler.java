package usecase.transfer.validation;

import usecase.transfer.TransferDataAccessInterface;
import usecase.transfer.TransferInputData;
import usecase.transfer.TransferOutputBoundary;

public class DestinationPortfolioExistHandler extends AbstractTransferValidationHandler {
    @Override
    protected boolean check(TransferInputData data, TransferDataAccessInterface dataAccess,
                            TransferOutputBoundary presenter) {
        boolean flag = true;
        if (!dataAccess.portfolioExists(data.username(), data.toPortfolio())) {
            presenter.prepareFailView("Destination portfolio does not exist: " + data.toPortfolio());
            flag = false;
        }
        return flag;
    }
}
