package interface_adapter.buyasset;

import use_case.get_price.GetPriceOutputBoundary;
import use_case.get_price.GetPriceOutputData;

public class GetPricePresenter implements GetPriceOutputBoundary {

    private final BuyAssetViewModel buyAssetViewModel;

    public GetPricePresenter(BuyAssetViewModel buyAssetViewModel) {
        this.buyAssetViewModel = buyAssetViewModel;
    }

    @Override
    public void presentPrice(GetPriceOutputData data) {
        BuyAssetState state = buyAssetViewModel.getState();
        state.price = data.getPrice();

        if (state.selectedQuantity != null && state.price > 0) {
            state.total = state.price * state.selectedQuantity;
        } else {
            state.total = 0.0;
        }

        state.errorMessage = null;

        buyAssetViewModel.setState(state);
        buyAssetViewModel.firePropertyChange();
    }

    @Override
    public void presentError(String message) {
        BuyAssetState state = buyAssetViewModel.getState();
        state.price = 0.0;
        state.total = 0.0;
        state.errorMessage = message;

        buyAssetViewModel.setState(state);
        buyAssetViewModel.firePropertyChange();
    }
}
