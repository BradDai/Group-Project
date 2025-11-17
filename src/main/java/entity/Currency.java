package entity;

public class Currency extends Asset{
    private String currencySymbol;

    public Currency(String type, double quantity,  String currencySymbol) {
        super(type, quantity);
        this.currencySymbol = currencySymbol;
    }
    public String getCurrencySymbol() {
        return currencySymbol;
    }
}
