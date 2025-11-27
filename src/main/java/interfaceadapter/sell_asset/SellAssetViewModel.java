package interfaceadapter.sell_asset;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import interfaceadapter.ViewModel;

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

    /**
     * The property change listener.
     * @param listener The PropertyChangeListener to be added
     */
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Fire property Changed.
     */
    public void firePropertyChanged() {
        propertyChangeSupport.firePropertyChange("state", null, this.sellAssetState);
    }

}
