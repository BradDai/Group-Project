//
//package use_case.transaction_history;
//
//import interface_adapter.history.HistoryState;
//
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
//        // ==== FAKE ROWS JUST FOR DEMO ====
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
//        // You can later actually filter 'rows' using inputData.getStartDate()/getEndDate()
//
//        String msg = "Loaded demo history for portfolio " + inputData.getPortfolio();
//        if (inputData.getStartDate() != null && inputData.getEndDate() != null) {
//            msg += " from " + inputData.getStartDate() + " to " + inputData.getEndDate();
//        }
//
//        TransactionHistoryOutputData output =
//                new TransactionHistoryOutputData(
//                        rows,
//                        msg,
//                        inputData.getStartDate(),
//                        inputData.getEndDate()
//                );
//
//        presenter.present(output);
//    }
//}
package use_case.transaction_history;

import data_access.TransactionDataAccessInterface;
import entity.transaction.BuyTransaction;
import entity.transaction.SellTransaction;
import entity.transaction.Transaction;
import interface_adapter.history.HistoryState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryInteractor implements TransactionHistoryInputBoundary {

    private final TransactionDataAccessInterface transactionRepo;
    private final TransactionHistoryOutputBoundary presenter;

    // TODO: replace with real logged-in user id when ready
    private final String userId = "demoUser";

    public TransactionHistoryInteractor(TransactionDataAccessInterface transactionRepo,
                                        TransactionHistoryOutputBoundary presenter) {
        this.transactionRepo = transactionRepo;
        this.presenter = presenter;
    }

    @Override
    public void execute(TransactionHistoryInputData inputData) {

        String portfolio = inputData.getPortfolio();
        String asset = inputData.getAsset();

        LocalDate start = parseDate(inputData.getStartDate());
        LocalDate end = parseDate(inputData.getEndDate());

        List<Transaction> txList = transactionRepo.getByFilters(
                userId,
                portfolio,
                asset,
                start,
                end
        );

        List<HistoryState.Row> rows = new ArrayList<>();
        for (Transaction tx : txList) {
            HistoryState.Row row = new HistoryState.Row();
            row.id = tx.getTransactionId();
            row.dateTime = tx.getDate().toString();

            if (tx instanceof BuyTransaction) {
                BuyTransaction bt = (BuyTransaction) tx;
                row.asset = bt.getAssetSymbol();
                row.type = "BUY";
                row.quantity = bt.getQuantity();
                row.totalValue = bt.getTotalValue();
            } else if (tx instanceof SellTransaction) {
                SellTransaction st = (SellTransaction) tx;
                row.asset = st.getAssetSymbol();
                row.type = "SELL";
                row.quantity = st.getQuantity();
                row.totalValue = st.getTotalValue();
            } else {
                row.asset = "";
                row.type = tx.getTransactionType();
                row.quantity = 0;
                row.totalValue = 0;
            }

            rows.add(row);
        }

        String msg = "Loaded " + rows.size() + " transactions for portfolio " + portfolio;
        if (start != null && end != null) {
            msg += " from " + start + " to " + end;
        }

        TransactionHistoryOutputData output =
                new TransactionHistoryOutputData(
                        rows,
                        msg,
                        inputData.getStartDate(),
                        inputData.getEndDate()
                );

        presenter.present(output);
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s);  // expects yyyy-MM-dd
    }
}

