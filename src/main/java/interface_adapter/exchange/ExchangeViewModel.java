package interface_adapter.exchange;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ExchangeViewModel {

    private String exchangeRate = "N/A";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setExchangeRate(String rate) {
        this.exchangeRate = rate;
    }


    public void firePropertyChanged() {
        support.firePropertyChange("exchangeRate", null, exchangeRate);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public String getViewName() {
        return "exchange";
    }
}
