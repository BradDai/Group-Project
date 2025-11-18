package use_case.transaction_history;

public class TransactionHistoryInputData {
    private final String portfolio;
    private final String asset;

    public TransactionHistoryInputData(String portfolio, String asset) {
        this.portfolio = portfolio;
        this.asset = asset;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public String getAsset() {
        return asset; //
    }
}
