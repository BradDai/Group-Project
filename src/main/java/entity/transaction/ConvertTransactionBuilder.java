package entity.transaction;

import java.time.LocalDateTime; /**
 * Builder for creating ConvertTransaction objects.
 */
public class ConvertTransactionBuilder {
    private String transactionId;
    private LocalDateTime date;
    private int portfolio;
    private String fromCurrency;
    private String toCurrency;
    private double fromAmount;
    private double exchangeRate;

    public ConvertTransactionBuilder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public ConvertTransactionBuilder setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public ConvertTransactionBuilder setPortfolio(int portfolio) {
        this.portfolio = portfolio;
        return this;
    }

    public ConvertTransactionBuilder setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
        return this;
    }

    public ConvertTransactionBuilder setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
        return this;
    }

    public ConvertTransactionBuilder setFromAmount(double fromAmount) {
        this.fromAmount = fromAmount;
        return this;
    }

    public ConvertTransactionBuilder setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
        return this;
    }

    public ConvertTransaction build() {
        return new ConvertTransaction(transactionId, date, portfolio,
                fromCurrency, toCurrency, fromAmount, exchangeRate);
    }
}
