package use_case.sell_asset;

public class SellAssetOutputData {
    private final String username;
    private final String assetName;
    private final double quantitySold;
    private final double totalPrice;
    private final double remainingQuantity;

    public SellAssetOutputData(final String username, final String assetName,
                               final double quantitySold, final double totalPrice, final double remainingQuantity) {
        this.username = username;
        this.assetName = assetName;
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
        this.remainingQuantity = remainingQuantity;
    }

    public String getUsername() {
        return username;
    }

    public String getAssetName() {
        return assetName;
    }

    public double getQuantitySold() {
        return quantitySold;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getRemainingQuantity() {
        return remainingQuantity;
    }

}
