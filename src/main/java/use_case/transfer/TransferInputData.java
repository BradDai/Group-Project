package use_case.transfer;

/**
 * Input Data for the Transfer Use Case.
 */
public class TransferInputData {
    private final String fromPortfolio;
    private final String toPortfolio;
    private final String transferType;
    private final String assetSymbol;
    private final double amount;

    public TransferInputData(String fromPortfolio, String toPortfolio,
                             String transferType, String assetSymbol, double amount) {
        this.fromPortfolio = fromPortfolio;
        this.toPortfolio = toPortfolio;
        this.transferType = transferType;
        this.assetSymbol = assetSymbol;
        this.amount = amount;
    }

    public String getFromPortfolio() {
        return fromPortfolio;
    }

    public String getToPortfolio() {
        return toPortfolio;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public double getAmount() {
        return amount;
    }
}