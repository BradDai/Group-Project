package entity.transaction;

import java.time.LocalDateTime;

/**
 * Abstract base class representing a transaction.
 */
public abstract class Transaction {
    private final String transactionId;
    private final LocalDateTime date;
    // CHANGED: Integer -> String
    private final String fromPortfolio;
    private final String toPortfolio;

    protected Transaction(String transactionId, LocalDateTime date, String fromPortfolio, String toPortfolio) {
        if (transactionId == null || "".equals(transactionId)) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.transactionId = transactionId;
        this.date = date;
        this.fromPortfolio = fromPortfolio;
        this.toPortfolio = toPortfolio;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    // CHANGED: Return type Integer -> String
    public String getFromPortfolio() {
        return fromPortfolio;
    }

    public String getToPortfolio() {
        return toPortfolio;
    }

    public abstract String getTransactionType();

    public abstract String getDescription();
}