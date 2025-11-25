package entity.transaction;

import java.time.LocalDateTime;

/**
 * Builder for creating SellTransaction objects.
 */
public class SellTransactionBuilder {
    private String transactionId;
    private LocalDateTime date;
    private String fromPortfolio;
    private String assetType;
    private String assetSymbol;
    private double quantity;
    private double pricePerUnit;

    public SellTransactionBuilder setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public SellTransactionBuilder setDate(final LocalDateTime date) {
        this.date = date;
        return this;
    }

    public SellTransactionBuilder setFromPortfolio(final String fromPortfolio) {
        this.fromPortfolio = fromPortfolio;
        return this;
    }

    public SellTransactionBuilder setAssetType(final String assetType) {
        this.assetType = assetType;
        return this;
    }

    public SellTransactionBuilder setAssetSymbol(final String assetSymbol) {
        this.assetSymbol = assetSymbol;
        return this;
    }

    public SellTransactionBuilder setQuantity(final double quantity) {
        this.quantity = quantity;
        return this;
    }

    public SellTransactionBuilder setPricePerUnit(final double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        return this;
    }

    public SellTransaction build() {
        return new SellTransaction(transactionId, date, fromPortfolio,
            assetType, assetSymbol, quantity, pricePerUnit);
    }
}
