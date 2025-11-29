package usecase.buyasset;

/**
 * Records the data.
 *
 * @param price price per unit
 * @param portfolioName .
 * @param quantity .
 * @param symbol .
 * @param username .
 */

public record BuyAssetInputData(String username, String portfolioName, String symbol, int quantity, double price) {
}
