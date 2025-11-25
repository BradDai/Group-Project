//package interface_adapter.history;
//
//import interface_adapter.ViewModel;
//
//public class HistoryViewModel extends ViewModel<HistoryState> {
//
//    public HistoryViewModel() {
//        super("history");
//        setState(new HistoryState());
//    }
//}
package interface_adapter.history;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class HistoryViewModel extends ViewModel {

    // view name used by ViewManager
    public static final String VIEW_NAME = "history";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private HistoryState state = new HistoryState();

    public HistoryViewModel() {
        super(VIEW_NAME);   // same pattern as your other ViewModels
    }

    // --- State getters / setters ---

    public HistoryState getState() {
        return state;
    }

    public void setState(HistoryState state) {
        this.state = state;
        firePropertyChanged();   // notify listeners whenever state changes
    }

    // --- PropertyChangeSupport wiring ---

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /** Called by the presenter after updating the state. */
    public void firePropertyChanged() {
        // "state" is the property name the view listens to
        support.firePropertyChange("state", null, state);
    }
}
