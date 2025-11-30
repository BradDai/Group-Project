package usecase.transfer.validation;

import usecase.transfer.TransferDataAccessInterface;
import usecase.transfer.TransferInputData;
import usecase.transfer.TransferOutputBoundary;

public class AssetExistsHandler extends AbstractTransferValidationHandler {
    @Override
    protected boolean check(TransferInputData data, TransferDataAccessInterface dataAccess,
                            TransferOutputBoundary presenter) {
        boolean flag = true;
        if (!dataAccess.hasAsset(data.username(), data.fromPortfolio(), data.assetSymbol())) {
            presenter.prepareFailView("Source portfolio does not contain asset: " + data.assetSymbol());
            flag = false;
        }
        return flag;
    }
}
