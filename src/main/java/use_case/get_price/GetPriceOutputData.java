package use_case.get_price;

public class GetPriceOutputData {
    private final double price;

    public GetPriceOutputData(final double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
