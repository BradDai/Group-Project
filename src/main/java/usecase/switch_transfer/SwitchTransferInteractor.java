package usecase.switch_transfer;

import usecase.transfer.TransferDataAccessInterface;

public class SwitchTransferInteractor implements SwitchTransferInputBoundary {

    private final SwitchTransferOutputBoundary switchTransferPresenter;
    private final TransferDataAccessInterface dataAccess;

    public SwitchTransferInteractor(final SwitchTransferOutputBoundary switchTransferOutputBoundary,
                                    final TransferDataAccessInterface dataAccess) {
        this.switchTransferPresenter = switchTransferOutputBoundary;
        this.dataAccess = dataAccess;
    }

    @Override
    public void switchToTransferView(final String username) {
        final String[] portfolios = dataAccess.getAvailablePortfolios(username);
        switchTransferPresenter.presentTransferView(username, portfolios);
    }
}
