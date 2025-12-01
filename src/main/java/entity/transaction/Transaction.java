package entity.transaction;

import java.time.LocalDateTime;

/**
 * Abstract base class representing a transaction.
 * Refactored to strictly follow LSP by removing unused fields/getters.
 */
public abstract class Transaction {

    private final String transactionId;
    private final LocalDateTime date;

    protected Transaction(final String transactionId, final LocalDateTime date) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.transactionId = transactionId;
        this.date = date;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public abstract String getDescription();
    public abstract String getTransactionType();
    public abstract String getAssetSymbol();
    public abstract double getQuantity();
}
