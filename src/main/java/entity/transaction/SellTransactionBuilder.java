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

    /**
     * I.
     * @param transactionId .
     * @return .
     */
    public SellTransactionBuilder setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    /**
     * I.
     * @param date .
     * @return .
     */
    public SellTransactionBuilder setDate(final LocalDateTime date) {
        this.date = date;
        return this;
    }

    /**
     * I.
     * @param fromPortfolio .
     * @return .
     */
    public SellTransactionBuilder setFromPortfolio(final String fromPortfolio) {
        this.fromPortfolio = fromPortfolio;
        return this;
    }

    /**
     * I.
     * @param assetType .
     * @return .
     */
    public SellTransactionBuilder setAssetType(final String assetType) {
        this.assetType = assetType;
        return this;
    }

    /**
     * L.
     * @param assetSymbol .
     * @return .
     */
    public SellTransactionBuilder setAssetSymbol(final String assetSymbol) {
        this.assetSymbol = assetSymbol;
        return this;
    }

    /**
     * L.
     * @param quantity .
     * @return .
     */
    public SellTransactionBuilder setQuantity(final double quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * I.
     * @param pricePerUnit .
     * @return .
     */
    public SellTransactionBuilder setPricePerUnit(final double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        return this;
    }

}
