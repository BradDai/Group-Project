package interface_adapter.transfer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The View Model for the Transfer View.
 * Holds the current {@link TransferState} and notifies listeners when the state changes.
 * This class is used by the controller and presenter to update the UI.
 */
public class TransferViewModel {

    /**
     * The name of the view associated with this ViewModel.
     */
    private final String viewName = "transfer";

    /**
     * Supports property change notifications for the View.
     */
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * The current state of the Transfer View.
     */
    private TransferState state = new TransferState();

    /**
     * Constructs a new TransferViewModel with an empty initial state.
     */
    public TransferViewModel() {
    }

    /**
     * Returns the name of the view associated with this ViewModel.
     *
     * @return the view name, always {@code "transfer"}
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Returns the current state of the Transfer View.
     *
     * @return the current {@link TransferState}
     */
    public TransferState getState() {
        return state;
    }

    /**
     * Sets a new state for the Transfer View.
     *
     * @param state the new {@link TransferState} to set
     */
    public void setState(final TransferState state) {
        this.state = state;
    }

    /**
     * Fires a property change event indicating that the entire state has changed.
     * Listeners will receive a property event with the name {@code "state"}.
     */
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    /**
     * Fires a property change event with a custom property name.
     *
     * @param propertyName the name of the property that has changed
     */
    public void firePropertyChanged(final String propertyName) {
        support.firePropertyChange(propertyName, null, this.state);
    }

    /**
     * Adds a listener that will be notified when the ViewModel state changes.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Removes a previously added property change listener.
     *
     * @param listener the listener to remove
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
