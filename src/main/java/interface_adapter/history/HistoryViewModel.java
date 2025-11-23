package interface_adapter.history;

import interface_adapter.ViewModel;

public class HistoryViewModel extends ViewModel<HistoryState> {

    public HistoryViewModel() {
        super("history");
        setState(new HistoryState());
    }
}
//package interface_adapter.history;
//
//import interface_adapter.ViewModel;
//
//import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;
//
//public class HistoryViewModel extends ViewModel {
//
//    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
//    private HistoryState state = new HistoryState();
//
//    public HistoryViewModel() {}
//
//    public HistoryState getState() {
//        return state;
//    }
//
//    public void setState(HistoryState state) {
//        this.state = state;
//        firePropertyChanged();
//    }
//
//    public void addPropertyChangeListener(PropertyChangeListener listener) {
//        support.addPropertyChangeListener(listener);
//    }
//
//    public void firePropertyChanged() {
//        support.firePropertyChange("state", null, state);
//    }
//}
//
