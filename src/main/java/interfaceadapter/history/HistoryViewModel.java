package interfaceadapter.history;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import interfaceadapter.ViewModel;

public class HistoryViewModel extends ViewModel {


    public static final String VIEW_NAME = "history";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private HistoryState state = new HistoryState();

    public HistoryViewModel() {
        super(VIEW_NAME);
    }

    public HistoryState getState() {
        return state;
    }

    public void setState(final HistoryState state) {
        this.state = state;
        firePropertyChanged();
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Called by the presenter after updating the state.
     */
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, state);
    }
}
