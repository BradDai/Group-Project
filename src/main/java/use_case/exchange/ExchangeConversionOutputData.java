package use_case.exchange;

import entity.SubAccount;

import java.util.ArrayList;
import java.util.List;

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

    public ExchangeConversionOutputData(String accountName,
                                        String from,
                                        String to,
                                        double amountGiven,
                                        double amountReceived,
                                        double rateUsed,
                                        double fromBalanceAfter,
                                        double toBalanceAfter,
                                        List<SubAccount> updatedAccounts) {
        this.accountName = accountName;
        this.from = from;
        this.to = to;
        this.amountGiven = amountGiven;
        this.amountReceived = amountReceived;
        this.rateUsed = rateUsed;
        this.fromBalanceAfter = fromBalanceAfter;
        this.toBalanceAfter = toBalanceAfter;
        this.updatedAccounts = updatedAccounts;
    }

    public String getAccountName() { return accountName; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getAmountGiven() { return amountGiven; }
    public double getAmountReceived() { return amountReceived; }
    public double getRateUsed() { return rateUsed; }
    public double getFromBalanceAfter() { return fromBalanceAfter; }
    public double getToBalanceAfter() { return toBalanceAfter; }
    public List<SubAccount> getUpdatedSubAccounts() {return updatedAccounts;}
}
