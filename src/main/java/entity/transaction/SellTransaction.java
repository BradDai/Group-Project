package entity.transaction;

import java.time.LocalDateTime;

public class SellTransaction extends Transaction {
    private final String fromPortfolio;
    private final String assetType;
    private final String assetSymbol;
    private final double quantity;
    private final double pricePerUnit;
    private final double totalValue;

    public SellTransaction(final String transactionId, final LocalDateTime date,
                           final String fromPortfolio, final String assetType, final String assetSymbol,
                           final double quantity, final double pricePerUnit) {
        super(transactionId, date);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (pricePerUnit <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        this.fromPortfolio = fromPortfolio;
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

    @Override
    public String getAssetSymbol() {
        return assetSymbol;
    }

    @Override
    public double getQuantity() {
        return quantity;
    }

    public String getFromPortfolio() {
        return fromPortfolio;
    }

    public String getAssetType() {
        return assetType;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public double getTotalValue() {
        return totalValue;
    }
}
