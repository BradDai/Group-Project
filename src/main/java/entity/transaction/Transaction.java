/// **
// * Abstract base class representing a transaction.
// */

package entity.transaction;

import java.time.LocalDateTime;

/**
 * Abstract base class representing a transaction.
 * You requested: keep all original fields and methods,
 * but allow adding new fields for history view + storage.
 */
public abstract class Transaction {

    private final String transactionId;
    private final LocalDateTime date;
    private final String fromPortfolio;
    private final String toPortfolio;

    private final String assetSymbol;
    private final Double quantity;
    private final Double priceAtTime;
    private final Double totalValue;
    private final String fromCurrency;
    private final String toCurrency;
    private final Double rate;
    private final Double amount;

    protected Transaction(
        final String transactionId,
        final LocalDateTime date,
        final String fromPortfolio,
        final String toPortfolio,
        final String assetSymbol,
        final Double quantity,
        final Double priceAtTime,
        final Double totalValue,
        final String fromCurrency,
        final String toCurrency,
        final Double rate,
        final Double amount
    ) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        this.transactionId = transactionId;
        this.date = date;
        this.fromPortfolio = fromPortfolio;
        this.toPortfolio = toPortfolio;
        this.assetSymbol = assetSymbol;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.totalValue = totalValue;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getFromPortfolio() {
        return fromPortfolio;
    }

    public String getToPortfolio() {
        return toPortfolio;
    }

    public abstract String getDescription();

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public Double getAmount() {
        return amount;
    }

    public abstract String getTransactionType(); }
