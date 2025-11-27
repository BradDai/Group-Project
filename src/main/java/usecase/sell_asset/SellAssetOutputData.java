package usecase.sell_asset;

public record SellAssetOutputData(String username, String assetName, double quantitySold, double totalPrice,
                                  double remainingQuantity) {

}
