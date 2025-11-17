package entity.transaction;

import java.time.LocalDateTime;

/**
 * Represents a transfer transaction between two portfolios.
 */
public class TransferTransaction extends Transaction {
    private final String assetType;
    private final double quantity;
    private final String assetSymbol;

    /**
     * Creates a new transfer transaction.
     * @param transactionId unique identifier
     * @param date transaction date
     * @param fromPortfolio source portfolio
     * @param toPortfolio destination portfolio
     * @param assetType type of asset being transferred
     * @param assetSymbol symbol of the asset
     * @param quantity amount transferred
     */
    public TransferTransaction(String transactionId, LocalDateTime date,
                               Integer fromPortfolio, Integer toPortfolio,
                               String assetType, String assetSymbol, double quantity) {
        super(transactionId, date, fromPortfolio, toPortfolio);
        if (fromPortfolio == null || "".equals(fromPortfolio)) {
            throw new IllegalArgumentException("Source portfolio cannot be empty");
        }
        if (toPortfolio == null || "".equals(toPortfolio)) {
            throw new IllegalArgumentException("Destination portfolio cannot be empty");
        }
        if (fromPortfolio.equals(toPortfolio)) {
            throw new IllegalArgumentException("Cannot transfer to the same portfolio");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.assetType = assetType;
        this.assetSymbol = assetSymbol;
        this.quantity = quantity;
    }

    @Override
    public String getTransactionType() {
        return "TRANSFER";
    }

    @Override
    public String getDescription() {
        return String.format("Transferred %.2f %s (%s) from %s to %s",
                quantity, assetType, assetSymbol,
                getFromPortfolio(), getToPortfolio());
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
}