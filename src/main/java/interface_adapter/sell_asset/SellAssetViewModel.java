package interface_adapter.sell_asset;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SellAssetViewModel extends ViewModel<SellAssetState> {

    private SellAssetState sellAssetState = new SellAssetState();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public SellAssetViewModel() {
        super("sellasset");
        setState(new SellAssetState());
    }

    public SellAssetState getState() {
        return sellAssetState;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        propertyChangeSupport.firePropertyChange("state", null, this.sellAssetState);
    }

}
