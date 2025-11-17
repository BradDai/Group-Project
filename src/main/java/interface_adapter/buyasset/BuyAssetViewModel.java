package interface_adapter.buyasset;

import interface_adapter.ViewModel;

public class BuyAssetViewModel extends ViewModel<BuyAssetState> {

    public BuyAssetViewModel() {
        super("buyasset");
        setState(new BuyAssetState());
    }
}
