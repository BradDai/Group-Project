package interfaceadapter.logged_in;

import usecase.switch_transfer.SwitchTransferInputBoundary;

public class SwitchTransferController {

    private final SwitchTransferInputBoundary switchTransferUseCaseInteractor;

    public SwitchTransferController(final SwitchTransferInputBoundary switchTransferUseCaseInteractor) {
        this.switchTransferUseCaseInteractor = switchTransferUseCaseInteractor;
    }

    /**
     * L.
     * @param username .
     */
    public void switchToTransferView(final String username) {
        switchTransferUseCaseInteractor.switchToTransferView(username);
    }
}
