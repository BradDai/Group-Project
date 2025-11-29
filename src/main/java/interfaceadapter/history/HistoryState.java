package interfaceadapter.history;

import java.util.ArrayList;
import java.util.List;

public class HistoryState {

    private String message = "";
    private List<Row> rows = new ArrayList<>();

    public static class Row {
        public String id;
        public String dateTime;
        public String asset;
        public String type;
        public double quantity;
        public double totalValue;
    }

    private List<String> portfolioOptions = new ArrayList<>();

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getPortfolioOptions() {
        return portfolioOptions;
    }

    public void setPortfolioOptions(List<String> portfolioOptions) {
        this.portfolioOptions = portfolioOptions;
    }
}
