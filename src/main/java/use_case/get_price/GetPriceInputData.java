package use_case.get_price;

public class GetPriceInputData {
    private final String symbol;

    public GetPriceInputData(final String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
