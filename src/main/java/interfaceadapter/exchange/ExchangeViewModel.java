package interfaceadapter.exchange;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ExchangeViewModel {

    private String exchangeRate = "N/A";

    private double rawRate = 0.0;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private ExchangeState exchangeState = new ExchangeState();

    public ExchangeState getExchangeState() {
        return exchangeState;
    }

    public void setExchangeRate(final String rate) {
        this.exchangeRate = rate;
    }

    public void setRawRate(double rate) {
        this.rawRate = rate;
    }

    public double getRawRate() {
        return rawRate;
    }

    public void firePropertyChangedRate() {
        support.firePropertyChange("exchangeRate", null, exchangeRate);
    }

    public void firePropertyChangedState() {
        support.firePropertyChange("exchangeState", null, this.exchangeState);
    }

    public void setState(final ExchangeState state) {
        this.exchangeState = state;
        support.firePropertyChange("exchangeState", null, this.exchangeState);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public String getViewName() {
        return "exchange";
    }
}
