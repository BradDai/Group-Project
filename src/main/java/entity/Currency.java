package entity;

public class Currency extends Asset {
    private final String currencySymbol;

    public Currency(final String type, final double quantity, final String currencySymbol) {
        super(type, quantity);
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }
}
