package entity.transaction;

import java.time.LocalDateTime;

/**
 * Abstract base class representing a transaction.
 */
public abstract class Transaction {
    private final String transactionId;
    private final LocalDateTime date;
    private final Integer fromPortfolio;
    private final Integer toPortfolio;

    /**
     * Creates a new transaction.
     * @param transactionId unique identifier for the transaction
     * @param date the date and time of the transaction
     * @param fromPortfolio the source portfolio number (can be null for buy transactions)
     * @param toPortfolio the destination portfolio number (can be null for sell transactions)
     */
    protected Transaction(String transactionId, LocalDateTime date, Integer fromPortfolio, Integer toPortfolio) {
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

    public Integer getFromPortfolio() {
        return fromPortfolio;
    }

    public Integer getToPortfolio() {
        return toPortfolio;
    }

    /**
     * Returns the type of transaction (e.g., "BUY", "SELL", "TRANSFER", "CONVERT").
     */
    public abstract String getTransactionType();

    /**
     * Returns a description of the transaction.
     */
    public abstract String getDescription();
}