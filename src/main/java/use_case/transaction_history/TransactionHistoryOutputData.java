package use_case.transaction_history;

import interface_adapter.history.HistoryState;
import java.util.List;

public class TransactionHistoryOutputData {

    private final List<HistoryState.Row> rows;
    private final String message;

    public TransactionHistoryOutputData(List<HistoryState.Row> rows, String message) {
        this.rows = rows;
        this.message = message;
    }

    public List<HistoryState.Row> getRows() {
        return rows; //
    }

    public String getMessage() {
        return message;
    }
}

