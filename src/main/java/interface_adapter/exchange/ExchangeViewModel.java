package interface_adapter.exchange;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ExchangeViewModel {

    private String exchangeRate = "N/A";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private ExchangeState exchangeState = new ExchangeState();

    public ExchangeState getExchangeState() {
        return exchangeState;
    }

    public void setExchangeRate(String rate) {
        this.exchangeRate = rate;
    }
    public void firePropertyChangedRate() {
        support.firePropertyChange("exchangeRate", null, exchangeRate);
    }

    public void firePropertyChangedState() {
        support.firePropertyChange("exchangeState", null, this.exchangeState);
    }

    public void setState(ExchangeState state) {
        this.exchangeState = state;
        support.firePropertyChange("exchangeState", null, this.exchangeState);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public String getViewName() {
        return "exchange";
    }
}
