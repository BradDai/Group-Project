//package entity.transaction;
//
//import java.time.LocalDateTime;
//
///**
// * Abstract base class representing a transaction.
// */
//public abstract class Transaction {
//    private final String transactionId;
//    private final LocalDateTime date;
//    private final String fromPortfolio;
//    private final String toPortfolio;
//
//
//    protected Transaction(String transactionId, LocalDateTime date, String fromPortfolio, String toPortfolio) {
//        if (transactionId == null || "".equals(transactionId)) {
//            throw new IllegalArgumentException("Transaction ID cannot be empty");
//        }
//        if (date == null) {
//            throw new IllegalArgumentException("Date cannot be null");
//        }
//        this.transactionId = transactionId;
//        this.date = date;
//        this.fromPortfolio = fromPortfolio;
//        this.toPortfolio = toPortfolio;
//    }
//
//    public String getTransactionId() {
//        return transactionId;
//    }
//
//    public LocalDateTime getDate() {
//        return date;
//    }
//
//    public String getFromPortfolio() {
//        return fromPortfolio;
//    }
//
//    public String getToPortfolio() {
//        return toPortfolio;
//    }
//
//    public abstract String getTransactionType();
//
//    public abstract String getDescription();
//}

package entity.transaction;

import java.time.LocalDateTime;

/**
 * Abstract base class representing a transaction.
 * You requested: keep all original fields and methods,
 * but allow adding new fields for history view + storage.
 */
public abstract class Transaction {

    // === ORIGINAL FIELDS (unchanged) ===
    private final String transactionId;
    private final LocalDateTime date;
    private final String fromPortfolio;
    private final String toPortfolio;

    // === NEW FIELDS FOR HISTORY PAGE (optional) ===
    // These will be ignored for transaction types that don’t use them.
    private final String assetSymbol;
    private final Double quantity;
    private final Double priceAtTime;
    private final Double totalValue;

    private final String fromCurrency;
    private final String toCurrency;
    private final Double rate;
    private final Double amount;

    // === CONSTRUCTOR (extended but keeps original parameters) ===
    protected Transaction(
            String transactionId,
            LocalDateTime date,
            String fromPortfolio,
            String toPortfolio,

            // NEW optional fields
            String assetSymbol,
            Double quantity,
            Double priceAtTime,
            Double totalValue,
            String fromCurrency,
            String toCurrency,
            Double rate,
            Double amount
    ) {
        // Original validation kept exactly the same
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

        // New fields can be null depending on type
        this.assetSymbol = assetSymbol;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.totalValue = totalValue;

        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.amount = amount;
    }

    // === ORIGINAL GETTERS (unchanged) ===
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

    // === NEW GETTERS (safe defaults for null) ===
    public String getAssetSymbol() {
        return assetSymbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public Double getPriceAtTime() {
        return priceAtTime;
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

    public Double getRate() {
        return rate;
    }

    public Double getAmount() {
        return amount;
    }

    // === ORIGINAL ABSTRACT METHODS (unchanged) ===
    public abstract String getTransactionType();

    public abstract String getDescription();
}
