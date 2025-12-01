package entity.transaction;

import java.time.LocalDateTime;

public class BuyTransaction extends Transaction {
    private final String toPortfolio;
    private final String assetType;
    private final String assetSymbol;
    private final double quantity;
    private final double pricePerUnit;
    private final double totalValue;

    public BuyTransaction(final String transactionId, final LocalDateTime date,
                          final String toPortfolio, final String assetType, final String assetSymbol,
                          final double quantity, final double pricePerUnit) {
        super(transactionId, date);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (pricePerUnit <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        this.toPortfolio = toPortfolio;
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

    @Override
    public String getAssetSymbol() {
        return assetSymbol;
    }

    @Override
    public double getQuantity() {
        return quantity;
    }

    public String getToPortfolio() {
        return toPortfolio;
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
