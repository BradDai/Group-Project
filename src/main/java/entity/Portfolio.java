package entity;

public class Portfolio {
    private final String name;
    private Currency[] currencies;
    private Stock[] stocks;

    public Portfolio(String name, Currency[] currencies, Stock[] stocks) {
        this.name = name;
        this.currencies = currencies;
        this.stocks = stocks;
    }

    public String getName() {
        return name;
    }

    public Stock[] getStocks() {
        return stocks;
    }

    public Currency[] getCurrencies() {
        return currencies;
    }
}
