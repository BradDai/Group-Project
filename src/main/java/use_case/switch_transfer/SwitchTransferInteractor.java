package use_case.switch_transfer;


public class SwitchTransferInteractor implements SwitchTransferInputBoundary {

    private final SwitchTransferOutputBoundary switchTransferPresenter;

    public SwitchTransferInteractor(SwitchTransferOutputBoundary switchTransferOutputBoundary) {
        this.switchTransferPresenter = switchTransferOutputBoundary;
    }

    public void switchToTransferView() { switchTransferPresenter.switchToTransferView(); }
}
