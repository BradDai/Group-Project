package interface_adapter.sell_asset;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.SubAccount.SubAccountDataAccessInterface;
import use_case.sell_asset.SellAssetOutputBoundary;
import use_case.sell_asset.SellAssetOutputData;
import use_case.sell_asset.SellAssetPriceOutputBoundary;
import use_case.sell_asset.SellAssetPriceOutputData;

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
        final String msg = "Successfully sold " + data.getQuantitySold() + " shares of "
            + data.getAssetName() + ". Total received: $"
            + String.format("%.2f", data.getTotalPrice())
            + ". Remaining: " + data.getRemainingQuantity();

        state.setMessage(msg);
        state.setErrorMessage(null);

        sellAssetViewModel.setState(state);
        sellAssetViewModel.firePropertyChanged();

        final String username = data.getAssetName();
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
        sellAssetState.setCurrentPrice(sellAssetPriceOutputData.getPrice());
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
