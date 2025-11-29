package interfaceadapter.buyasset;

import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.SubAccount.SubAccountDataAccessInterface;
import usecase.buyasset.BuyAssetOutputBoundary;
import usecase.buyasset.BuyAssetOutputData;

public class BuyAssetPresenter implements BuyAssetOutputBoundary {

    private final BuyAssetViewModel buyAssetViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final SubAccountDataAccessInterface subAccountDao;

    public BuyAssetPresenter(final BuyAssetViewModel buyAssetViewModel,
                             final LoggedInViewModel loggedInViewModel,
                             final SubAccountDataAccessInterface subAccountDao) {
        this.buyAssetViewModel = buyAssetViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.subAccountDao = subAccountDao;
    }

    @Override
    public void presentSuccess(final BuyAssetOutputData outputData) {
        final BuyAssetState buyState = buyAssetViewModel.getState();
        buyState.purchaseMessage = outputData.message();
        buyState.errorMessage = null;
        buyAssetViewModel.setState(buyState);
        buyAssetViewModel.firePropertyChange();
        final String username = outputData.username();
        final LoggedInState loggedState = loggedInViewModel.getState();
        loggedState.setSubAccounts(subAccountDao.getSubAccountsOf(username));
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
