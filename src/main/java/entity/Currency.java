package entity;

public class Currency extends Asset{
    private String currencySymbol;

    public Currency(String type, double quantity,  String companySymbol) {
        super(type, quantity);
        this.currencySymbol = companySymbol;
    }
    public String getCurrencySymbol() {
        return currencySymbol;
    }
}
