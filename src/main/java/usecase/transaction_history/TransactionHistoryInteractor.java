package usecase.transaction_history;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dataaccess.TransactionDataAccessInterface;
import entity.transaction.BuyTransaction;
import entity.transaction.ConvertTransaction;
import entity.transaction.SellTransaction;
import entity.transaction.Transaction;
import entity.transaction.TransferTransaction;
import interfaceadapter.history.HistoryState;
import interfaceadapter.logged_in.LoggedInViewModel;

public class TransactionHistoryInteractor implements TransactionHistoryInputBoundary {

    private final TransactionDataAccessInterface transactionRepo;
    private final TransactionHistoryOutputBoundary presenter;
    private final LoggedInViewModel loggedInViewModel;

    public TransactionHistoryInteractor(final TransactionDataAccessInterface transactionRepo,
                                        final TransactionHistoryOutputBoundary presenter,
                                        final LoggedInViewModel loggedInViewModel) {
        this.transactionRepo = transactionRepo;
        this.presenter = presenter;
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void execute(final TransactionHistoryInputData inputData) {

        final String portfolio = inputData.portfolio();
        final String asset = inputData.asset();
        final LocalDate start = parseDate(inputData.startDate());
        final LocalDate end = parseDate(inputData.endDate());

        System.out.println("[Interactor] execute() called");
        System.out.println("  portfolio = " + portfolio);
        System.out.println("  asset     = " + asset);
        System.out.println("  startDate = " + start);
        System.out.println("  endDate   = " + end);

        // use real logged-in username
        final String username = loggedInViewModel.getState().getUsername();
        System.out.println("  username  = " + username);

        final List<Transaction> txList = transactionRepo.getByFilters(
            username,
            portfolio,
            asset,
            start,
            end
        );

        System.out.println("[Interactor] DAO returned " + txList.size() + " transactions");

        // map to HistoryState.Row
        final List<HistoryState.Row> rows = new ArrayList<>();
        for (final Transaction tx : txList) {
            final HistoryState.Row row = new HistoryState.Row();
            row.id = tx.getTransactionId();
            row.dateTime = tx.getDate().toString();

            if (tx instanceof final BuyTransaction bt) {
                row.asset = bt.getAssetSymbol();
                row.type = "BUY";
                row.quantity = bt.getQuantity();
                row.totalValue = bt.getTotalValue();
            }
            else if (tx instanceof final SellTransaction st) {
                row.asset = st.getAssetSymbol();
                row.type = "SELL";
                row.quantity = st.getQuantity();
                row.totalValue = st.getTotalValue();
            }
            // ⭐ handle currency conversions
            else if (tx instanceof final ConvertTransaction ct) {
                row.asset = ct.getFromCurrency() + "->" + ct.getToCurrency();
                row.type = ct.getTransactionType();      // "CONVERT"
                row.quantity = ct.getFromAmount();       // source amount
                row.totalValue = ct.getToAmount();       // target amount
            }
            // ⭐ NEW: handle transfers
            else if (tx instanceof final TransferTransaction tt) {
                row.asset = tt.getAssetSymbol();         // stock symbol or currency code
                row.type = "TRANSFER";
                row.quantity = tt.getQuantity();         // amount moved
                row.totalValue = 0.0;                    // or whatever you decide
            }
            else {
                // any other future types
                row.asset = "";
                row.type = tx.getTransactionType();
                row.quantity = 0.0;
                row.totalValue = 0.0;
            }
            rows.add(row);
        }


        // build output & send to presenter
        String msg = "Loaded " + rows.size() + " transactions for portfolio " + portfolio;
        if (start != null && end != null) {
            msg += " from " + start + " to " + end;
        }

        final TransactionHistoryOutputData output =
            new TransactionHistoryOutputData(
                rows,
                msg,
                inputData.startDate(),
                inputData.endDate()
            );

        System.out.println("[Interactor] message = " + msg);
        System.out.println("[Interactor] calling presenter.present(...)");
        presenter.present(output);
        System.out.println("[Interactor] execute() finished");
    }

    private LocalDate parseDate(final String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        // expects yyyy-MM-dd
        return LocalDate.parse(s);
    }
}
