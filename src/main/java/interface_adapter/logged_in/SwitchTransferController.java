package interface_adapter.logged_in;

import use_case.switch_transfer.SwitchTransferInputBoundary;

public class SwitchTransferController {

    private final SwitchTransferInputBoundary switchTransferUseCaseInteractor;

    public SwitchTransferController(SwitchTransferInputBoundary switchTransferUseCaseInteractor) {
        this.switchTransferUseCaseInteractor = switchTransferUseCaseInteractor;
    }

    public void switchToTransferView(String username) {
        switchTransferUseCaseInteractor.switchToTransferView(username);
    }
}