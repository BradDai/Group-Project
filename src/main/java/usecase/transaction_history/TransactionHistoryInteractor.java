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
import entity.transaction.TransactionUtility;
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

    // This currently has a Checkstyle error: "Executable statement count is 37 (max allowed is 30)". After doing some
    // of the suggested fixes, this should be solved.
    @Override
    public void execute(final TransactionHistoryInputData inputData) {

        // All these variables are being used less than 3 times. They could be made as inline variables to improve
        // code quality.
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

        // This list is being used only at the end of this method. For refactoring, it would be better to use the
        // "Slide statements" technique, as it is not very useful to have this variable here with no uses.
        final List<HistoryState.Row> rows = new ArrayList<>();
        for (final Transaction tx : txList) {
            final HistoryState.Row row = new HistoryState.Row();
            row.id = tx.getTransactionId();
            row.dateTime = tx.getDate().toString();

            // The next 5 conditions are being used to check what type of row to create, with very similar lines. This
            // could be improved with a Factory design pattern, as it would separate the creation of these objects, and
            // handle all of that data. Also, the types seem to be a "magic" string, so you could maybe add at the
            // Constants file, and reuse them here.
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

        // The output variable is also being used only once. Making it as inline variable would improve the code
        // quality.
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
        }
        else {
            final String username = loggedInViewModel.getState().getUsername();
            if (username == null || username.isBlank()) {
                presenter.presentPortfolioOptions(new ArrayList<>());
            }
            else {

                // this variable is also being used only once, so making it as an inline variable would be good.
                final List<Transaction> allTx = transactionRepo.getByFilters(
                    username,
                    null,
                    null,
                    null,
                    null
                );
                final Set<String> portfolioIds = new LinkedHashSet<>();
                for (Transaction tx : allTx) {
                    final String from = TransactionUtility.getFromPortfolio(tx);
                    if (from != null && !from.isBlank()) {
                        portfolioIds.add(from);
                    }
                    final String to = TransactionUtility.getToPortfolio(tx);
                    if (to != null && !to.isBlank()) {
                        portfolioIds.add(to);
                    }
                }
                // This variable is being used only once, so it would be better to have it as an inline variable.
                final ArrayList<String> result = new ArrayList<>(portfolioIds);
                presenter.presentPortfolioOptions(result);
            }
        }

    }

    private LocalDate parseDate(final String sss) {
        LocalDate result = null;
        if (sss != null && !sss.isBlank()) {
            result = LocalDate.parse(sss);
        }
        return result;
    }
}
