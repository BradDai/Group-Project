/*
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
*/

package use_case.transaction_history;

public class TransactionHistoryInputData {
    private final String portfolio;
    private final String asset;
    private final String startDate;
    private final String endDate;

    public TransactionHistoryInputData(String portfolio,
                                       String asset,
                                       String startDate,
                                       String endDate) {
        this.portfolio = portfolio;
        this.asset = asset;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public String getAsset() {
        return asset;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}

