package entity;

public class Stock extends Asset {
    private String companySymbol;

    public Stock(String type, double quantity, String companySymbol) {
        super(type, quantity);
        this.companySymbol = companySymbol;
    }
    public String getCompanySymbol() {
        return companySymbol;
    }
}
