package entity.transaction;

import java.time.LocalDateTime;

public class TransferTransaction extends Transaction {
    private final String assetType;
    private final String assetSymbol;
    private final double quantity;

    public TransferTransaction(final String transactionId, final LocalDateTime date,
                               final String fromPortfolio, final String toPortfolio,
                               final String assetType, final String assetSymbol, final double quantity) {
        super(
            transactionId,
            date,
            fromPortfolio,
            toPortfolio,
            assetSymbol,
            quantity,
            null,
            null,
            null,
            null,
            null,
            null
        );

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
