package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.transfer.TransferState;
import interface_adapter.transfer.TransferViewModel;
import use_case.switch_transfer.SwitchTransferOutputBoundary;

public class SwitchTransferPresenter implements SwitchTransferOutputBoundary {

    private final TransferViewModel transferViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchTransferPresenter(TransferViewModel transferViewModel, ViewManagerModel viewManagerModel) {
        this.transferViewModel = transferViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void presentTransferView(String username, String[] portfolios) {
        TransferState state = transferViewModel.getState();
        state.setUsername(username);
        state.setAvailablePortfolios(portfolios);
        state.setError("");

        // --- FIX: Clear amount on entry ---
        state.setAmount("");
        // ----------------------------------

        if (portfolios != null && portfolios.length > 0) {
            state.setFromPortfolio(portfolios[0]);
            state.setToPortfolio(portfolios[0]);
        }

        transferViewModel.setState(state);
        transferViewModel.firePropertyChanged();

        viewManagerModel.setState(transferViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}