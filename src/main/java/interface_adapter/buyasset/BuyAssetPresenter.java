package interface_adapter.buyasset;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.SubAccount.SubAccountDataAccessInterface;
import use_case.buyasset.BuyAssetOutputBoundary;
import use_case.buyasset.BuyAssetOutputData;

public class BuyAssetPresenter implements BuyAssetOutputBoundary {

    private final BuyAssetViewModel buyAssetViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final SubAccountDataAccessInterface subAccountDAO;

    public BuyAssetPresenter(final BuyAssetViewModel buyAssetViewModel,
                             final LoggedInViewModel loggedInViewModel,
                             final SubAccountDataAccessInterface subAccountDAO) {
        this.buyAssetViewModel = buyAssetViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.subAccountDAO = subAccountDAO;
    }

    @Override
    public void presentSuccess(final BuyAssetOutputData outputData) {
        final BuyAssetState buyState = buyAssetViewModel.getState();
        buyState.purchaseMessage = outputData.getMessage();
        buyState.errorMessage = null;
        buyAssetViewModel.setState(buyState);
        buyAssetViewModel.firePropertyChange();
        final String username = outputData.getUsername();
        final LoggedInState loggedState = loggedInViewModel.getState();
        loggedState.setSubAccounts(subAccountDAO.getSubAccountsOf(username));
        loggedInViewModel.setState(loggedState);
        loggedInViewModel.firePropertyChange(LoggedInViewModel.SUBACCOUNTS_CHANGED);
    }

    @Override
    public void presentFail(final String errorMessage) {
        final BuyAssetState buyState = buyAssetViewModel.getState();
        buyState.errorMessage = errorMessage;
        buyState.purchaseMessage = null;

        buyAssetViewModel.setState(buyState);
        buyAssetViewModel.firePropertyChange();
    }
}
