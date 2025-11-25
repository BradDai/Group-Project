package use_case.sell_asset;

public class SellAssetPriceOutputData {
    private final double price;

    public SellAssetPriceOutputData(final double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
