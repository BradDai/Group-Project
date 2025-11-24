package interface_adapter.sell_asset;

import use_case.sell_asset.SellAssetOutputBoundary;
import use_case.sell_asset.SellAssetOutputData;
import use_case.sell_asset.SellAssetPriceOutputBoundary;
import use_case.sell_asset.SellAssetPriceOutputData;

public class SellAssetPresenter implements SellAssetOutputBoundary, SellAssetPriceOutputBoundary {

    private final SellAssetViewModel sellAssetViewModel;

    public SellAssetPresenter(final SellAssetViewModel sellAssetViewModel) {
        this.sellAssetViewModel = sellAssetViewModel;
    }

    @Override
    public void prepareSuccessView(final SellAssetOutputData data) {
        final SellAssetState state = sellAssetViewModel.getState();

        // Construct a user-friendly message
        final String msg = "Successfully sold " + data.getQuantitySold() + " shares of "
            + data.getAssetName() + ". Total received: $"
            + String.format("%.2f", data.getTotalPrice()) +
            ". Remaining: " + data.getRemainingQuantity();

        state.setMessage(msg);
        state.setErrorMessage(null);

        sellAssetViewModel.setState(state);
        sellAssetViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailureView(final String errorMessage) {
        final SellAssetState state = sellAssetViewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setMessage(null);  // clear success message

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
