package usecase.transfer.validation;

import usecase.transfer.TransferDataAccessInterface;
import usecase.transfer.TransferInputData;
import usecase.transfer.TransferOutputBoundary;

public class SufficientBalanceHandler extends AbstractTransferValidationHandler {
    @Override
    protected boolean check(TransferInputData data, TransferDataAccessInterface dataAccess,
                            TransferOutputBoundary presenter) {
        boolean flag = true;
        final double availableBalance = dataAccess.getAssetBalance(data.username(), data.fromPortfolio(),
                data.assetSymbol());
        if (availableBalance < data.amount()) {
            presenter.prepareFailView(String.format("Insufficient balance. Available: %.2f", availableBalance));
            flag = false;
        }
        return flag;
    }
}
