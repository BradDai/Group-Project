package interface_adapter.sellasset;

import interface_adapter.ViewModel;

public class SellAssetViewModel extends ViewModel<SellAssetState> {

    public SellAssetViewModel() {
        super("sellasset");
        setState(new SellAssetState());
    }

}
