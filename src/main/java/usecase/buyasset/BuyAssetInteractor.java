package usecase.buyasset;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import dataaccess.TransactionDataAccessInterface;
import entity.Stock;
import entity.SubAccount;
import entity.transaction.BuyTransaction;
import usecase.SubAccount.SubAccountDataAccessInterface;

public class BuyAssetInteractor implements BuyAssetInputBoundary {

    private final SubAccountDataAccessInterface subAccountDAO;
    private final TransactionDataAccessInterface transactionDAO;
    private final BuyAssetOutputBoundary presenter;

    public BuyAssetInteractor(final SubAccountDataAccessInterface subAccountDAO,
                              final TransactionDataAccessInterface transactionDAO,
                              final BuyAssetOutputBoundary presenter) {
        this.subAccountDAO = subAccountDAO;
        this.transactionDAO = transactionDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(final BuyAssetInputData inputData) {

        final String username = inputData.username();
        final String portfolioName = inputData.portfolioName();
        final String symbol = inputData.symbol();
        final int qty = inputData.quantity();
        final double price = inputData.price();

        if (username == null || username.isEmpty()) {
            presenter.presentFail("No user logged in.");
            return;
        }
        if (portfolioName == null || portfolioName.isEmpty()) {
            presenter.presentFail("Please choose a portfolio.");
            return;
        }
        if (symbol == null || symbol.isEmpty()) {
            presenter.presentFail("Please choose an asset.");
            return;
        }
        if (qty <= 0) {
            presenter.presentFail("Quantity must be positive.");
            return;
        }
        if (price <= 0) {
            presenter.presentFail("Price not loaded.");
            return;
        }

        final List<SubAccount> accounts = subAccountDAO.getSubAccountsOf(username);
        SubAccount target = null;
        for (final SubAccount sa : accounts) {
            if (sa.getName().equalsIgnoreCase(portfolioName)) {
                target = sa;
                break;
            }
        }

        if (target == null) {
            presenter.presentFail("Portfolio not found.");
            return;
        }

        final BigDecimal cost = BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(qty));

        if (target.getBalanceUSD().compareTo(cost) < 0) {
            presenter.presentFail("Insufficient funds.");
            return;
        }

        final BigDecimal newBal = target.getBalanceUSD().subtract(cost);
        target.setBalanceUSD(newBal);

        // update holdings
        final Stock stock = new Stock(symbol, qty, symbol);
        target.addOrIncreaseAsset(stock);

        subAccountDAO.save(username, target);

        final BuyTransaction tx = new BuyTransaction(
            generateTransactionId(),
            LocalDateTime.now(),
            portfolioName,
            "STOCK",
            symbol,
            qty,
            price
        );

        System.out.println("[BuyAssetInteractor] Saving transaction: " + tx.getTransactionId());

        transactionDAO.save(username,
            new BuyTransaction(
                "TX-" + System.currentTimeMillis(),
                LocalDateTime.now(),
                portfolioName,
                "Stock",
                symbol,
                qty,
                price
            )
        );

        presenter.presentSuccess(
            new BuyAssetOutputData(
                "Purchased " + qty + " of " + symbol + " for $" + cost + " in " + portfolioName + ".",
                username
            )
        );
    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis();
    }
}
