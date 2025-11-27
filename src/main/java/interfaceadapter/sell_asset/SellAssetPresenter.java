package interfaceadapter.sell_asset;

import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import usecase.SubAccount.SubAccountDataAccessInterface;
import usecase.sell_asset.SellAssetOutputBoundary;
import usecase.sell_asset.SellAssetOutputData;
import usecase.sell_asset.SellAssetPriceOutputBoundary;
import usecase.sell_asset.SellAssetPriceOutputData;

public class SellAssetPresenter implements SellAssetOutputBoundary, SellAssetPriceOutputBoundary {

    private final SellAssetViewModel sellAssetViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final SubAccountDataAccessInterface dataAccess;

    public SellAssetPresenter(final SellAssetViewModel sellAssetViewModel,
                              final LoggedInViewModel loggedInViewModel,
                              final SubAccountDataAccessInterface dataAccess) {
        this.sellAssetViewModel = sellAssetViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.dataAccess = dataAccess;
    }

    @Override
    public void prepareSuccessView(final SellAssetOutputData data) {
        final SellAssetState state = sellAssetViewModel.getState();

        // Construct a user-friendly message
        final String msg = "Successfully sold " + data.quantitySold() + " shares of "
            + data.assetName() + ". Total received: $"
            + String.format("%.2f", data.totalPrice())
            + ". Remaining: " + data.remainingQuantity();

        state.setMessage(msg);
        state.setErrorMessage(null);

        sellAssetViewModel.setState(state);
        sellAssetViewModel.firePropertyChanged();

        final String username = data.assetName();
        final LoggedInState loggedInState = loggedInViewModel.getState();
        loggedInState.setSubAccounts(dataAccess.getSubAccountsOf(username));
        loggedInViewModel.setState(loggedInState);
        loggedInViewModel.firePropertyChange(LoggedInViewModel.SUBACCOUNTS_CHANGED);
    }

    @Override
    public void prepareFailureView(final String errorMessage) {
        final SellAssetState state = sellAssetViewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setMessage(null);

        sellAssetViewModel.setState(state);
        sellAssetViewModel.firePropertyChanged();
    }

    @Override
    public void preparePriceSuccessView(final SellAssetPriceOutputData sellAssetPriceOutputData) {
        final SellAssetState sellAssetState = sellAssetViewModel.getState();
        sellAssetState.setCurrentPrice(sellAssetPriceOutputData.price());
        sellAssetState.setPriceError(null);

        sellAssetViewModel.setState(sellAssetState);
        sellAssetViewModel.firePropertyChanged();
    }

    @Override
    public void preparePriceFailureView(final String errorMessage) {
        final SellAssetState sellAssetState = sellAssetViewModel.getState();
        sellAssetState.setPriceError(errorMessage);
        sellAssetState.setCurrentPrice(0.0);

        sellAssetViewModel.setState(sellAssetState);
        sellAssetViewModel.firePropertyChanged();
    }
}
