package entity.transaction;

import java.time.LocalDateTime;

/**
 * Represents a buy transaction where an asset is purchased.
 */
public class BuyTransaction extends Transaction {
    private final String assetType;
    private final double quantity;
    private final double pricePerUnit;
    private final double totalValue;
    private final String assetSymbol;

    /**
     * Creates a new buy transaction.
     * @param transactionId unique identifier
     * @param date transaction date
     * @param toPortfolio portfolio receiving the asset
     * @param assetType type of asset (e.g., "Stock", "Currency")
     * @param assetSymbol symbol of the asset (e.g., "AAPL", "USD")
     * @param quantity amount purchased
     * @param pricePerUnit price per unit of asset
     */
    public BuyTransaction(String transactionId, LocalDateTime date,
                          String toPortfolio, String assetType, String assetSymbol,
                          double quantity, double pricePerUnit) {
        super(transactionId, date, null, toPortfolio);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (pricePerUnit <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.assetType = assetType;
        this.assetSymbol = assetSymbol;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.totalValue = quantity * pricePerUnit;
    }

    @Override
    public String getTransactionType() {
        return "BUY";
    }

    @Override
    public String getDescription() {
        return String.format("Bought %.2f %s (%s) at $%.2f per unit",
                quantity, assetType, assetSymbol, pricePerUnit);
    }

    public String getAssetType() {
        return assetType;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public double getTotalValue() {
        return totalValue;
    }
}