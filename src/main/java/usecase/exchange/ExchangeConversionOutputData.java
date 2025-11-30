package usecase.exchange;

import java.util.List;

import entity.SubAccount;

public record ExchangeConversionOutputData(String accountName, String from, String to, double amountGiven,
                                           double amountReceived, double rateUsed, double fromBalanceAfter,
                                           double toBalanceAfter, List<SubAccount> updatedAccounts) {

    public List<SubAccount> getUpdatedSubAccounts() {
        return updatedAccounts;
    }
}
