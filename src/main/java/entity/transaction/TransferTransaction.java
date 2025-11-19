package entity.transaction;

import java.time.LocalDateTime;

public class TransferTransaction extends Transaction {
    private final String assetType;
    private final String assetSymbol;
    private final double quantity;

    // CHANGED: Constructor accepts String for portfolios
    public TransferTransaction(String transactionId, LocalDateTime date, String fromPortfolio, String toPortfolio,
                               String assetType, String assetSymbol, double quantity) {
        super(transactionId, date, fromPortfolio, toPortfolio);
        this.assetType = assetType;
        this.assetSymbol = assetSymbol;
        this.quantity = quantity;
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

    @Override
    public String getTransactionType() {
        return "TRANSFER";
    }

    @Override
    public String getDescription() {
        return String.format("Transfer of %.2f %s from %s to %s",
                quantity, assetSymbol, getFromPortfolio(), getToPortfolio());
    }
}