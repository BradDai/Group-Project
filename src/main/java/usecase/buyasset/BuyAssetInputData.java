package usecase.buyasset;

/**
 * @param price price per unit
 */
public record BuyAssetInputData(String username, String portfolioName, String symbol, int quantity, double price) {
}
