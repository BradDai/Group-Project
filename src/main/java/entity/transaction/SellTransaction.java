package entity.transaction;

import java.time.LocalDateTime;

/**
 * Represents a sell transaction where an asset is sold.
 */
public class SellTransaction extends Transaction {
    private final String assetType;
    private final double quantity;
    private final double pricePerUnit;
    private final double totalValue;
    private final String assetSymbol;

    /**
     * Creates a new sell transaction.
     * @param transactionId unique identifier
     * @param date transaction date
     * @param fromPortfolio portfolio selling the asset
     * @param assetType type of asset
     * @param assetSymbol symbol of the asset
     * @param quantity amount sold
     * @param pricePerUnit price per unit of asset
     */
    public SellTransaction(String transactionId, LocalDateTime date,
                           Integer fromPortfolio, String assetType, String assetSymbol,
                           double quantity, double pricePerUnit) {
        super(transactionId, date, fromPortfolio, null);
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
        return "SELL";
    }

    @Override
    public String getDescription() {
        return String.format("Sold %.2f %s (%s) at $%.2f per unit",
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