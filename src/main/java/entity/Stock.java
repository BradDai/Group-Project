package entity;

public class Stock extends Asset {
    private final String companySymbol;

    public Stock(final String type, final double quantity, final String companySymbol) {
        super(type, quantity);
        this.companySymbol = companySymbol;
    }

    public String getCompanySymbol() {
        return companySymbol;
    }
}
