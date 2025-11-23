package use_case.switch_transfer;

import use_case.transfer.TransferDataAccessInterface;

public class SwitchTransferInteractor implements SwitchTransferInputBoundary {

    private final SwitchTransferOutputBoundary switchTransferPresenter;
    private final TransferDataAccessInterface dataAccess;
    public SwitchTransferInteractor(SwitchTransferOutputBoundary switchTransferOutputBoundary,
                                    TransferDataAccessInterface dataAccess) {
        this.switchTransferPresenter = switchTransferOutputBoundary;
        this.dataAccess = dataAccess;
    }

    @Override
    public void switchToTransferView(String username) {
        String[] portfolios = dataAccess.getAvailablePortfolios(username);
        switchTransferPresenter.presentTransferView(username, portfolios);
    }
}