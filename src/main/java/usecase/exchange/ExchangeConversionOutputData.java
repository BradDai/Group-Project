package usecase.exchange;

import java.util.List;

import entity.SubAccount;

public class ExchangeConversionOutputData {

    private final String accountName;
    private final String from;
    private final String to;
    private final double amountGiven;
    private final double amountReceived;
    private final double rateUsed;
    private final double fromBalanceAfter;
    private final double toBalanceAfter;
    private final List<SubAccount> updatedAccounts;

    public ExchangeConversionOutputData(final String accountName,
                                        final String from,
                                        final String tto,
                                        final double amountGiven,
                                        final double amountReceived,
                                        final double rateUsed,
                                        final double fromBalanceAfter,
                                        final double toBalanceAfter,
                                        final List<SubAccount> updatedAccounts) {
        this.accountName = accountName;
        this.from = from;
        this.to = tto;
        this.amountGiven = amountGiven;
        this.amountReceived = amountReceived;
        this.rateUsed = rateUsed;
        this.fromBalanceAfter = fromBalanceAfter;
        this.toBalanceAfter = toBalanceAfter;
        this.updatedAccounts = updatedAccounts;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getAmountGiven() {
        return amountGiven;
    }

    public double getAmountReceived() {
        return amountReceived;
    }

    public double getRateUsed() {
        return rateUsed;
    }

    public double getFromBalanceAfter() {
        return fromBalanceAfter;
    }

    public double getToBalanceAfter() {
        return toBalanceAfter;
    }

    public List<SubAccount> getUpdatedSubAccounts() {
        return updatedAccounts;
    }
}
