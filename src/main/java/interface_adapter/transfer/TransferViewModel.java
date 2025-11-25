package interface_adapter.transfer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The View Model for the Transfer View.
 */
public class TransferViewModel {
    private final String viewName = "transfer";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private TransferState state = new TransferState();

    public TransferViewModel() {
    }

    public String getViewName() {
        return viewName;
    }

    public TransferState getState() {
        return state;
    }

    public void setState(final TransferState state) {
        this.state = state;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    public void firePropertyChanged(final String propertyName) {
        support.firePropertyChange(propertyName, null, this.state);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
