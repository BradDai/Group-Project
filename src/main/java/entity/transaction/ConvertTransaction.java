package entity.transaction;

import java.time.LocalDateTime;

/**
 * Represents a currency conversion transaction.
 */
public class ConvertTransaction extends Transaction {
    private final String fromCurrency;
    private final String toCurrency;
    private final double fromAmount;
    private final double toAmount;
    private final double exchangeRate;

    /**
     * Creates a new convert transaction.
     * @param transactionId unique identifier
     * @param date transaction date
     * @param portfolio portfolio where conversion occurs
     * @param fromCurrency source currency symbol
     * @param toCurrency destination currency symbol
     * @param fromAmount amount of source currency
     * @param exchangeRate conversion rate
     */
    public ConvertTransaction(String transactionId, LocalDateTime date,
                              Integer portfolio, String fromCurrency,
                              String toCurrency, double fromAmount,
                              double exchangeRate) {
        super(transactionId, date, portfolio, portfolio);
        if (fromAmount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (exchangeRate <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.fromAmount = fromAmount;
        this.exchangeRate = exchangeRate;
        this.toAmount = fromAmount * exchangeRate;
    }

    @Override
    public String getTransactionType() {
        return "CONVERT";
    }

    @Override
    public String getDescription() {
        return String.format("Converted %.2f %s to %.2f %s (rate: %.4f)",
                fromAmount, fromCurrency, toAmount, toCurrency, exchangeRate);
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public double getFromAmount() {
        return fromAmount;
    }

    public double getToAmount() {
        return toAmount;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }
}