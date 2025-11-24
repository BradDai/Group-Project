package interface_adapter.sell_asset;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import interface_adapter.ViewModel;

public class SellAssetViewModel extends ViewModel<SellAssetState> {

    private final SellAssetState sellAssetState = new SellAssetState();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public SellAssetViewModel() {
        super("sellasset");
        setState(new SellAssetState());
    }

    public SellAssetState getState() {
        return sellAssetState;
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        propertyChangeSupport.firePropertyChange("state", null, this.sellAssetState);
    }

}
