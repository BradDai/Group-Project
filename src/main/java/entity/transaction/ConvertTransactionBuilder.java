package entity.transaction;

import java.time.LocalDateTime;

/**
 * Builder for creating ConvertTransaction objects.
 */
public class ConvertTransactionBuilder {
    private String transactionId;
    private LocalDateTime date;
    private String portfolio;
    private String fromCurrency;
    private String toCurrency;
    private double fromAmount;
    private double exchangeRate;

    public ConvertTransactionBuilder setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public ConvertTransactionBuilder setDate(final LocalDateTime date) {
        this.date = date;
        return this;
    }

    public ConvertTransactionBuilder setPortfolio(final String portfolio) {
        this.portfolio = portfolio;
        return this;
    }

    public ConvertTransactionBuilder setFromCurrency(final String fromCurrency) {
        this.fromCurrency = fromCurrency;
        return this;
    }

    public ConvertTransactionBuilder setToCurrency(final String toCurrency) {
        this.toCurrency = toCurrency;
        return this;
    }

    public ConvertTransactionBuilder setFromAmount(final double fromAmount) {
        this.fromAmount = fromAmount;
        return this;
    }

    public ConvertTransactionBuilder setExchangeRate(final double exchangeRate) {
        this.exchangeRate = exchangeRate;
        return this;
    }

    public ConvertTransaction build() {
        return new ConvertTransaction(transactionId, date, portfolio,
            fromCurrency, toCurrency, fromAmount, exchangeRate);
    }
}
