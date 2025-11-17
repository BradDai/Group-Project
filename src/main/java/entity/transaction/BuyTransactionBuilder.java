package entity.transaction;

import java.time.LocalDateTime;

/**
 * Builder for creating BuyTransaction objects.
 */
public class BuyTransactionBuilder {
    private String transactionId;
    private LocalDateTime date;
    private int toPortfolio;
    private String assetType;
    private String assetSymbol;
    private double quantity;
    private double pricePerUnit;

    public BuyTransactionBuilder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public BuyTransactionBuilder setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public BuyTransactionBuilder setToPortfolio(int toPortfolio) {
        this.toPortfolio = toPortfolio;
        return this;
    }

    public BuyTransactionBuilder setAssetType(String assetType) {
        this.assetType = assetType;
        return this;
    }

    public BuyTransactionBuilder setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
        return this;
    }

    public BuyTransactionBuilder setQuantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public BuyTransactionBuilder setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        return this;
    }

    public BuyTransaction build() {
        return new BuyTransaction(transactionId, date, toPortfolio,
                assetType, assetSymbol, quantity, pricePerUnit);
    }
}

