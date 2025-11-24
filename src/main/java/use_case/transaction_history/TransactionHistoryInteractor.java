//package use_case.transaction_history;
//
//import interface_adapter.history.HistoryState;
//import java.util.ArrayList;
//import java.util.List;
//
//public class TransactionHistoryInteractor implements TransactionHistoryInputBoundary {
//
//    private final TransactionHistoryOutputBoundary presenter;
//
//    public TransactionHistoryInteractor(TransactionHistoryOutputBoundary presenter) {
//        this.presenter = presenter;
//    }
//
//    @Override
//    public void execute(TransactionHistoryInputData inputData) {
//
//        List<HistoryState.Row> rows = new ArrayList<>();
//
//        HistoryState.Row r1 = new HistoryState.Row();
//        r1.id = "T1";
//        r1.dateTime = "2025-11-02";
//        r1.asset = "AAPL";
//        r1.type = "BUY";
//        r1.quantity = 10;
//        r1.totalValue = 1500;
//        rows.add(r1);
//
//        HistoryState.Row r2 = new HistoryState.Row();
//        r2.id = "T2";
//        r2.dateTime = "2025-11-03";
//        r2.asset = "TSLA";
//        r2.type = "SELL";
//        r2.quantity = 5;
//        r2.totalValue = 1100;
//        rows.add(r2);
//
//        TransactionHistoryOutputData output =
//                new TransactionHistoryOutputData(
//                        rows,
//                        "Loaded demo history for portfolio: " + inputData.getPortfolio()
//                );
//
//        presenter.present(output); //
//    }
//}

package use_case.transaction_history;

import java.util.ArrayList;
import java.util.List;

import interface_adapter.history.HistoryState;

public class TransactionHistoryInteractor implements TransactionHistoryInputBoundary {

    private final TransactionHistoryOutputBoundary presenter;

    public TransactionHistoryInteractor(final TransactionHistoryOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(final TransactionHistoryInputData inputData) {

        // ==== FAKE ROWS JUST FOR DEMO ====
        final List<HistoryState.Row> rows = new ArrayList<>();

        final HistoryState.Row r1 = new HistoryState.Row();
        r1.id = "T1";
        r1.dateTime = "2025-11-02";
        r1.asset = "AAPL";
        r1.type = "BUY";
        r1.quantity = 10;
        r1.totalValue = 1500;
        rows.add(r1);

        final HistoryState.Row r2 = new HistoryState.Row();
        r2.id = "T2";
        r2.dateTime = "2025-11-03";
        r2.asset = "TSLA";
        r2.type = "SELL";
        r2.quantity = 5;
        r2.totalValue = 1100;
        rows.add(r2);

        // You can later actually filter 'rows' using inputData.getStartDate()/getEndDate()

        String msg = "Loaded demo history for portfolio " + inputData.getPortfolio();
        if (inputData.getStartDate() != null && inputData.getEndDate() != null) {
            msg += " from " + inputData.getStartDate() + " to " + inputData.getEndDate();
        }

        final TransactionHistoryOutputData output =
            new TransactionHistoryOutputData(
                rows,
                msg,
                inputData.getStartDate(),
                inputData.getEndDate()
            );

        presenter.present(output);
    }
}
