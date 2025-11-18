package entity.transaction;

import java.time.LocalDateTime; /**
 * Builder for creating SellTransaction objects.
 */
public class SellTransactionBuilder {
    private String transactionId;
    private LocalDateTime date;
    private int fromPortfolio;
    private String assetType;
    private String assetSymbol;
    private double quantity;
    private double pricePerUnit;

    public SellTransactionBuilder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public SellTransactionBuilder setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public SellTransactionBuilder setFromPortfolio(int fromPortfolio) {
        this.fromPortfolio = fromPortfolio;
        return this;
    }

    public SellTransactionBuilder setAssetType(String assetType) {
        this.assetType = assetType;
        return this;
    }

    public SellTransactionBuilder setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
        return this;
    }

    public SellTransactionBuilder setQuantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public SellTransactionBuilder setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        return this;
    }

    public SellTransaction build() {
        return new SellTransaction(transactionId, date, fromPortfolio,
                assetType, assetSymbol, quantity, pricePerUnit);
    }
}
