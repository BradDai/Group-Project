
package usecase.transaction_history;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

        final String username = loggedInViewModel.getState().getUsername();

        final List<Transaction> txList = transactionRepo.getByFilters(
                username,
                portfolio,
                asset,
                start,
                end
        );

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
            else if (tx instanceof final ConvertTransaction ct) {
                row.asset = ct.getFromCurrency() + "->" + ct.getToCurrency();
                row.type = ct.getTransactionType();
                row.quantity = ct.getFromAmount();
                row.totalValue = ct.getToAmount();
            }
            else if (tx instanceof final TransferTransaction tt) {
                row.asset = tt.getAssetSymbol();
                row.type = "TRANSFER";
                row.quantity = tt.getQuantity();
                row.totalValue = 0.0;
            }
            else {
                row.asset = "";
                row.type = tx.getTransactionType();
                row.quantity = 0.0;
                row.totalValue = 0.0;
            }

            rows.add(row);
        }

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

        presenter.present(output);
    }

    @Override
    public void loadPortfolioOptions() {

        if (loggedInViewModel == null || loggedInViewModel.getState() == null) {
            presenter.presentPortfolioOptions(new ArrayList<>());
            return;
        }

        final String username = loggedInViewModel.getState().getUsername();

        if (username == null || username.isBlank()) {
            presenter.presentPortfolioOptions(new ArrayList<>());
            return;
        }

        final List<Transaction> allTx = transactionRepo.getByFilters(
                username,
                null,
                null,
                null,
                null
        );

        final Set<String> portfolioIds = new LinkedHashSet<>();
        for (Transaction tx : allTx) {
            if (tx.getFromPortfolio() != null && !tx.getFromPortfolio().isBlank()) {
                portfolioIds.add(tx.getFromPortfolio());
            }
            if (tx.getToPortfolio() != null && !tx.getToPortfolio().isBlank()) {
                portfolioIds.add(tx.getToPortfolio());
            }
        }

        final ArrayList<String> result = new ArrayList<>(portfolioIds);
        presenter.presentPortfolioOptions(result);
    }

    private LocalDate parseDate(final String sss) {
        if (sss == null || sss.isBlank()) {
            return null;
        }
        return LocalDate.parse(sss);
    }
}




