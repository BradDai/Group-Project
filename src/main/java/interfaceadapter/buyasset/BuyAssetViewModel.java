package interfaceadapter.buyasset;

import interfaceadapter.ViewModel;

public class BuyAssetViewModel extends ViewModel<BuyAssetState> {

    public BuyAssetViewModel() {
        super("buyasset");
        setState(new BuyAssetState());
    }
}
