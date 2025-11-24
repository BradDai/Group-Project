package interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The ViewModel for our CA implementation.
 * This class delegates work to a PropertyChangeSupport object for
 * managing the property change events.
 *
 * @param <T> The type of state object contained in the model.
 */
public class ViewModel<T> {

    private final String viewName;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private T state;

    public ViewModel(final String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return this.viewName;
    }

    public T getState() {
        return this.state;
    }

    public void setState(final T state) {
        this.state = state;
    }

    /**
     * Fires a property changed event for the state of this ViewModel.
     */
    public void firePropertyChange() {
        this.support.firePropertyChange("state", null, this.state);
    }

    /**
     * Fires a property changed event for the state of this ViewModel, which
     * allows the user to specify a different propertyName.
     *
     * @param propertyName the label for the property that was changed
     */
    public void firePropertyChange(final String propertyName) {
        this.support.firePropertyChange(propertyName, null, this.state);
    }

    /**
     * Fires a property changed event with specific old and new values.
     *
     * @param propertyName The programmatic name of the property that was changed.
     * @param oldValue     The old value of the property.
     * @param newValue     The new value of the property.
     */
    public void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        this.support.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Adds a PropertyChangeListener to this ViewModel.
     *
     * @param listener The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }
}
