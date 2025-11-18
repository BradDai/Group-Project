package interface_adapter.history;

import java.util.ArrayList;
import java.util.List;

public class HistoryState {

    private String message = "";
    private List<Row> rows = new ArrayList<>();

    // This is the type you are using in TransactionHistoryInteractor
    public static class Row {
        public String id;
        public String dateTime;
        public String asset;
        public String type;
        public int quantity;
        public double totalValue;
    }

    public HistoryState() {
    }

    // message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // rows
    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
