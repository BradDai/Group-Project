package use_case.transfer;

import entity.SubAccount;
import java.util.List;

public class TransferOutputData {
    private final String transactionId;
    private final String fromPortfolio;
    private final String toPortfolio;
    private final String assetSymbol;
    private final double amount;
    private final boolean success;
    private final List<SubAccount> updatedAccounts;

    public TransferOutputData(String transactionId, String fromPortfolio,
                              String toPortfolio, String assetSymbol,
                              double amount, boolean success,
                              List<SubAccount> updatedAccounts) {
        this.transactionId = transactionId;
        this.fromPortfolio = fromPortfolio;
        this.toPortfolio = toPortfolio;
        this.assetSymbol = assetSymbol;
        this.amount = amount;
        this.success = success;
        this.updatedAccounts = updatedAccounts;
    }

    public String getTransactionId() { return transactionId; }

    public String getFromPortfolio() { return fromPortfolio; }

    public String getToPortfolio() { return toPortfolio; }

    public String getAssetSymbol() { return assetSymbol; }

    public double getAmount() { return amount; }

    public boolean isSuccess() { return success; }

    public List<SubAccount> getUpdatedAccounts() { return updatedAccounts; }
}