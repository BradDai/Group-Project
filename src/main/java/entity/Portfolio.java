package entity;

public class Portfolio {
    private final String name;
    private final Currency[] currencies;
    private final Stock[] stocks;

    public Portfolio(final String name, final Currency[] currencies, final Stock[] stocks) {
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
