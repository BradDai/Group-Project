package entity.transaction;

import java.time.LocalDateTime;

/**
 * Builder class for creating {@link TransferTransaction} objects.
 */
public class TransferTransactionBuilder {
    private String transactionId;
    private LocalDateTime date;
    private String fromPortfolio;
    private String toPortfolio;
    private String assetType;
    private String assetSymbol;
    private double quantity;

    /**
     * Sets the transaction ID.
     *
     * @param transactionId the unique identifier of the transaction
     * @return this builder instance
     */
    public TransferTransactionBuilder setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    /**
     * Sets the transaction date.
     *
     * @param date the date and time of the transaction
     * @return this builder instance
     */
    public TransferTransactionBuilder setDate(final LocalDateTime date) {
        this.date = date;
        return this;
    }

    /**
     * Sets the portfolio from which the asset is transferred.
     *
     * @param fromPortfolio the name of the source portfolio
     * @return this builder instance
     */
    public TransferTransactionBuilder setFromPortfolio(final String fromPortfolio) {
        this.fromPortfolio = fromPortfolio;
        return this;
    }

    /**
     * Sets the portfolio to which the asset is transferred.
     *
     * @param toPortfolio the name of the destination portfolio
     * @return this builder instance
     */
    public TransferTransactionBuilder setToPortfolio(final String toPortfolio) {
        this.toPortfolio = toPortfolio;
        return this;
    }

    /**
     * Sets the type of the transferred asset (e.g., stock, currency).
     *
     * @param assetType the type of asset
     * @return this builder instance
     */
    public TransferTransactionBuilder setAssetType(final String assetType) {
        this.assetType = assetType;
        return this;
    }

    /**
     * Sets the symbol of the asset being transferred.
     *
     * @param assetSymbol the asset symbol (e.g., AAPL, USD)
     * @return this builder instance
     */
    public TransferTransactionBuilder setAssetSymbol(final String assetSymbol) {
        this.assetSymbol = assetSymbol;
        return this;
    }

    /**
     * Sets the quantity of the asset being transferred.
     *
     * @param quantity the number of units transferred
     * @return this builder instance
     */
    public TransferTransactionBuilder setQuantity(final double quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * Builds and returns a new {@link TransferTransaction} object.
     *
     * @return a fully constructed {@link TransferTransaction}
     */
    public TransferTransaction build() {
        return new TransferTransaction(transactionId, date, fromPortfolio,
            toPortfolio, assetType, assetSymbol, quantity);
    }
}
