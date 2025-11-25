package entity.transaction;

import java.time.LocalDateTime;

/**
 * Builder for creating BuyTransaction objects.
 */
public class BuyTransactionBuilder {
    private String transactionId;
    private LocalDateTime date;
    private String toPortfolio;
    private String assetType;
    private String assetSymbol;
    private double quantity;
    private double pricePerUnit;

    public BuyTransactionBuilder setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public BuyTransactionBuilder setDate(final LocalDateTime date) {
        this.date = date;
        return this;
    }

    public BuyTransactionBuilder setToPortfolio(final String toPortfolio) {
        this.toPortfolio = toPortfolio;
        return this;
    }

    public BuyTransactionBuilder setAssetType(final String assetType) {
        this.assetType = assetType;
        return this;
    }

    public BuyTransactionBuilder setAssetSymbol(final String assetSymbol) {
        this.assetSymbol = assetSymbol;
        return this;
    }

    public BuyTransactionBuilder setQuantity(final double quantity) {
        this.quantity = quantity;
        return this;
    }

    public BuyTransactionBuilder setPricePerUnit(final double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        return this;
    }

    public BuyTransaction build() {
        return new BuyTransaction(transactionId, date, toPortfolio,
            assetType, assetSymbol, quantity, pricePerUnit);
    }
}

