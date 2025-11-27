package interfaceadapter.logged_in;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.transfer.TransferState;
import interfaceadapter.transfer.TransferViewModel;
import usecase.switch_transfer.SwitchTransferOutputBoundary;

public class SwitchTransferPresenter implements SwitchTransferOutputBoundary {

    private final TransferViewModel transferViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchTransferPresenter(final TransferViewModel transferViewModel, final ViewManagerModel viewManagerModel) {
        this.transferViewModel = transferViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void presentTransferView(final String username, final String[] portfolios) {
        final TransferState state = transferViewModel.getState();
        state.setUsername(username);
        state.setAvailablePortfolios(portfolios);
        state.setError("");

        // --- FIX: Clear amount on entry ---
        state.setAmount("");
        // ----------------------------------

        if (portfolios != null && portfolios.length > 0) {
            state.setFromPortfolio();
            state.setToPortfolio();
        }

        transferViewModel.setState(state);
        transferViewModel.firePropertyChanged();

        viewManagerModel.setState(transferViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
