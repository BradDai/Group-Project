package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.transfer.TransferViewModel;
import use_case.switch_transfer.SwitchTransferOutputBoundary;

public class SwitchTransferPresenter implements SwitchTransferOutputBoundary {

    private final TransferViewModel transferViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchTransferPresenter(TransferViewModel transferViewModel, ViewManagerModel viewManagerModel) {

        this.transferViewModel = transferViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    public void switchToTransferView() {

        viewManagerModel.setState(transferViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
