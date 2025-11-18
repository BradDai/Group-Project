package use_case.sell_asset;

public class SellAssetOutputData {
    private final String assetName;
    private final double quantitySold;
    private final double totalPrice;
    private final double remainingQuantity;

    public SellAssetOutputData(String assetName, double quantitySold, double totalPrice, double remainingQuantity) {
        this.assetName = assetName;
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
        this.remainingQuantity = remainingQuantity;
    }

    public String getAssetName() {return assetName;}
    public double getQuantitySold() {return quantitySold;}
    public double getTotalPrice() {return totalPrice;}
    public double getRemainingQuantity() {return remainingQuantity;}

}
