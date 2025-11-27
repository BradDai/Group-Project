package usecase.transaction_history;

import java.util.List;

import interfaceadapter.history.HistoryState;

public record TransactionHistoryOutputData(List<HistoryState.Row> rows, String message, String startDate,
                                           String endDate) {

}

