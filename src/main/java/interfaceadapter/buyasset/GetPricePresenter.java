package interfaceadapter.buyasset;

import usecase.get_price.GetPriceOutputBoundary;
import usecase.get_price.GetPriceOutputData;

public class GetPricePresenter implements GetPriceOutputBoundary {

    private final BuyAssetViewModel buyAssetViewModel;

    public GetPricePresenter(final BuyAssetViewModel buyAssetViewModel) {
        this.buyAssetViewModel = buyAssetViewModel;
    }

    @Override
    public void presentPrice(final GetPriceOutputData data) {
        final BuyAssetState state = buyAssetViewModel.getState();
        state.price = data.price();

        if (state.selectedQuantity != null && state.price > 0) {
            state.total = state.price * state.selectedQuantity;
        }
        else {
            state.total = 0.0;
        }

        state.errorMessage = null;

        buyAssetViewModel.setState(state);
        buyAssetViewModel.firePropertyChange();
    }

    @Override
    public void presentError(final String message) {
        final BuyAssetState state = buyAssetViewModel.getState();
        state.price = 0.0;
        state.total = 0.0;
        state.errorMessage = message;

        buyAssetViewModel.setState(state);
        buyAssetViewModel.firePropertyChange();
    }
}
