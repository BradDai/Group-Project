package usecase.transfer;

import java.util.List;

import entity.SubAccount;

public record TransferOutputData(String transactionId, String fromPortfolio, String toPortfolio, String assetSymbol,
                                 double amount, boolean success, List<SubAccount> updatedAccounts) {
}
