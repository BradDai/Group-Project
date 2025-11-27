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
     *
     * @param transactionId unique identifier
     * @param date          transaction date/time
     * @param portfolio     portfolio where conversion occurs
     * @param fromCurrency  source currency symbol
     * @param toCurrency    destination currency symbol
     * @param fromAmount    amount of source currency
     * @param exchangeRate  conversion rate (toCurrency per 1 fromCurrency)
     */
    public ConvertTransaction(final String transactionId, final LocalDateTime date,
                              final String portfolio, final String fromCurrency,
                              final String toCurrency, final double fromAmount,
                              final double exchangeRate) {
        // For the base Transaction, we treat it as happening within one portfolio.
        // We leave assetSymbol/price/total as null; we'll override the getters below.
        super(
            transactionId,
            date,
            portfolio,          // fromPortfolio
            portfolio,          // toPortfolio
            null,               // assetSymbol (N/A for base)
            0.0,                // quantity (we override getQuantity)
            null,               // pricePerUnit (we override getPricePerUnit if needed)
            null,               // totalValue (we override getTotalValue)
            fromCurrency,
            toCurrency,
            exchangeRate,
            fromAmount
        );

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

    // ⭐ NEW: make history table show meaningful values

    @Override
    public String getAssetSymbol() {
        // Show the currency pair in the Asset column, e.g. "USD->CAD"
        return fromCurrency + "->" + toCurrency;
    }

    @Override
    public double getQuantity() {
        // Quantity = amount of source currency spent
        return fromAmount;
    }

    @Override
    public double getTotalValue() {
        // Total = amount of target currency received
        return toAmount;
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

