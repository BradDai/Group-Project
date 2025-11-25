//package use_case.transaction_history;
//
//import interface_adapter.history.HistoryState;
//import java.util.List;
//
//public class TransactionHistoryOutputData {
//
//    private final List<HistoryState.Row> rows;
//    private final String message;
//
//    public TransactionHistoryOutputData(List<HistoryState.Row> rows, String message) {
//        this.rows = rows;
//        this.message = message;
//    }
//
//    public List<HistoryState.Row> getRows() {
//        return rows; //
//    }
//
//    public String getMessage() {
//        return message;
//    }
//}

package use_case.transaction_history;

import java.util.List;

import interface_adapter.history.HistoryState;

public class TransactionHistoryOutputData {

    private final List<HistoryState.Row> rows;
    private final String message;
    private final String startDate;
    private final String endDate;

    public TransactionHistoryOutputData(final List<HistoryState.Row> rows,
                                        final String message,
                                        final String startDate,
                                        final String endDate) {
        this.rows = rows;
        this.message = message;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<HistoryState.Row> getRows() {
        return rows;
    }

    public String getMessage() {
        return message;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}

