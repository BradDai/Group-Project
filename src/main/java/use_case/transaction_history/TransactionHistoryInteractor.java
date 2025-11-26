//
//package use_case.transaction_history;
//
//import data_access.TransactionDataAccessInterface;
//import interface_adapter.history.HistoryState;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TransactionHistoryInteractor implements TransactionHistoryInputBoundary {
//
//    private final TransactionHistoryOutputBoundary presenter;
//
//    public TransactionHistoryInteractor(TransactionDataAccessInterface transactionDataAccessObject, TransactionHistoryOutputBoundary presenter) {
//        this.presenter = presenter;
//    }
//
//    @Override
//    public void execute(TransactionHistoryInputData inputData) {
//
//        // ===== DEBUG: incoming request =====
//        System.out.println("[Interactor] execute() called");
//        System.out.println("  portfolio = " + inputData.getPortfolio());
//        System.out.println("  asset     = " + inputData.getAsset());
//        System.out.println("  startDate = " + inputData.getStartDate());
//        System.out.println("  endDate   = " + inputData.getEndDate());
//
//        // ==== FAKE ROWS JUST FOR DEMO ====
//        List<HistoryState.Row> rows = new ArrayList<>();
//
//        HistoryState.Row r1 = new HistoryState.Row();
//        r1.id = "T1";
//        r1.dateTime = "2025-11-02";
//        r1.asset = "AAPL";
//        r1.type = "BUY";
//        r1.quantity = 10
package use_case.transaction_history;

import data_access.TransactionDataAccessInterface;
import entity.transaction.BuyTransaction;
import entity.transaction.ConvertTransaction;
import entity.transaction.SellTransaction;
import entity.transaction.Transaction;
import interface_adapter.history.HistoryState;
import interface_adapter.logged_in.LoggedInViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryInteractor implements TransactionHistoryInputBoundary {

    private final TransactionDataAccessInterface transactionRepo;
    private final TransactionHistoryOutputBoundary presenter;
    private final LoggedInViewModel loggedInViewModel;

    public TransactionHistoryInteractor(TransactionDataAccessInterface transactionRepo,
                                        TransactionHistoryOutputBoundary presenter,
                                        LoggedInViewModel loggedInViewModel) {
        this.transactionRepo = transactionRepo;
        this.presenter = presenter;
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void execute(TransactionHistoryInputData inputData) {

        String portfolio = inputData.getPortfolio();
        String asset = inputData.getAsset();
        LocalDate start = parseDate(inputData.getStartDate());
        LocalDate end   = parseDate(inputData.getEndDate());

        System.out.println("[Interactor] execute() called");
        System.out.println("  portfolio = " + portfolio);
        System.out.println("  asset     = " + asset);
        System.out.println("  startDate = " + start);
        System.out.println("  endDate   = " + end);

        // use real logged-in username
        String username = loggedInViewModel.getState().getUsername();
        System.out.println("  username  = " + username);

        List<Transaction> txList = transactionRepo.getByFilters(
                username,
                portfolio,
                asset,
                start,
                end
        );

        System.out.println("[Interactor] DAO returned " + txList.size() + " transactions");

        // map to HistoryState.Row
        List<HistoryState.Row> rows = new ArrayList<>();
        for (Transaction tx : txList) {
            HistoryState.Row row = new HistoryState.Row();
            row.id = tx.getTransactionId();
            row.dateTime = tx.getDate().toString();

            if (tx instanceof BuyTransaction bt) {
                row.asset = bt.getAssetSymbol();
                row.type = "BUY";
                row.quantity = bt.getQuantity();
                row.totalValue = bt.getTotalValue();
            } else if (tx instanceof SellTransaction st) {
                row.asset = st.getAssetSymbol();
                row.type = "SELL";
                row.quantity = st.getQuantity();
                row.totalValue = st.getTotalValue();
            }
            // ⭐ NEW: handle currency conversions
            else if (tx instanceof ConvertTransaction ct) {
                row.asset = ct.getFromCurrency() + "->" + ct.getToCurrency();
                row.type = ct.getTransactionType();      // "CONVERT"
                row.quantity = ct.getFromAmount();       // amount of source currency
                row.totalValue = ct.getToAmount();       // amount of target currency
            } else {
                // other types (transfer etc.) – minimal info
                row.asset = "";
                row.type = tx.getTransactionType();
                row.quantity = 0.0;
                row.totalValue = 0.0;
            }

            System.out.println("  [row] id=" + row.id +
                    ", dateTime=" + row.dateTime +
                    ", asset=" + row.asset +
                    ", type=" + row.type +
                    ", qty=" + row.quantity +
                    ", total=" + row.totalValue);

            rows.add(row);
        }

        // build output & send to presenter
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

        System.out.println("[Interactor] message = " + msg);
        System.out.println("[Interactor] calling presenter.present(...)");
        presenter.present(output);
        System.out.println("[Interactor] execute() finished");
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        // expects yyyy-MM-dd
        return LocalDate.parse(s);
    }
}
