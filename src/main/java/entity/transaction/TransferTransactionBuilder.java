package entity.transaction;

import java.time.LocalDateTime;

public class TransferTransactionBuilder {
    private String transactionId;
    private LocalDateTime date;
    private String fromPortfolio;
    private String toPortfolio;
    private String assetType;
    private String assetSymbol;
    private double quantity;

    public TransferTransactionBuilder setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public TransferTransactionBuilder setDate(final LocalDateTime date) {
        this.date = date;
        return this;
    }

    public TransferTransactionBuilder setFromPortfolio(final String fromPortfolio) {
        this.fromPortfolio = fromPortfolio;
        return this;
    }

    public TransferTransactionBuilder setToPortfolio(final String toPortfolio) {
        this.toPortfolio = toPortfolio;
        return this;
    }

    public TransferTransactionBuilder setAssetType(final String assetType) {
        this.assetType = assetType;
        return this;
    }

    public TransferTransactionBuilder setAssetSymbol(final String assetSymbol) {
        this.assetSymbol = assetSymbol;
        return this;
    }

    public TransferTransactionBuilder setQuantity(final double quantity) {
        this.quantity = quantity;
        return this;
    }

    public TransferTransaction build() {
        return new TransferTransaction(transactionId, date, fromPortfolio, toPortfolio, assetType, assetSymbol,
            quantity);
    }
}
