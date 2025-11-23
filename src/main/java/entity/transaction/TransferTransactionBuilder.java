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

    public TransferTransactionBuilder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public TransferTransactionBuilder setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public TransferTransactionBuilder setFromPortfolio(String fromPortfolio) {
        this.fromPortfolio = fromPortfolio;
        return this;
    }

    public TransferTransactionBuilder setToPortfolio(String toPortfolio) {
        this.toPortfolio = toPortfolio;
        return this;
    }

    public TransferTransactionBuilder setAssetType(String assetType) {
        this.assetType = assetType;
        return this;
    }

    public TransferTransactionBuilder setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
        return this;
    }

    public TransferTransactionBuilder setQuantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public TransferTransaction build() {
        return new TransferTransaction(transactionId, date, fromPortfolio, toPortfolio, assetType, assetSymbol, quantity);
    }
}