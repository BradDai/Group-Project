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

    private final SubAccountDataAccessInterface subAccountDao;
    private final TransactionDataAccessInterface transactionDao;
    private final BuyAssetOutputBoundary presenter;

    public BuyAssetInteractor(final SubAccountDataAccessInterface subAccountDao,
                              final TransactionDataAccessInterface transactionDao,
                              final BuyAssetOutputBoundary presenter) {
        this.subAccountDao = subAccountDao;
        this.transactionDao = transactionDao;
        this.presenter = presenter;
    }

    @Override
    // There's a checkstyle error about "Cyclomatic Complexity is 13 (max allowed is 10)", so this should be fixed.
    // With one of the recommendations I made below (using a Chain of Responsibility design pattern), this should be
    // automatically fixed, as all conditions are going to be handled in a separate class.
    public void execute(final BuyAssetInputData inputData) {

        final String username = inputData.username();
        final String portfolioName = inputData.portfolioName();
        final String symbol = inputData.symbol();
        final int qty = inputData.quantity();
        final double price = inputData.price();

        // Since there are a lot of conditions being tested, a design pattern could be implemented to improve the code
        // quality. One example is the Chain of Responsibility, a behavioral pattern for a sequence of requests, like
        // you have here for the conditions. An example was used on the TransferInteractor, so you could get some
        // inspirations from that.
        if (username == null || username.isEmpty()) {
            presenter.presentFail("No user logged in.");
        }
        else if (portfolioName == null || portfolioName.isEmpty()) {
            presenter.presentFail("Please choose a portfolio.");
        }
        else if (symbol == null || symbol.isEmpty()) {
            presenter.presentFail("Please choose an asset.");
        }
        else if (qty <= 0) {
            presenter.presentFail("Quantity must be positive.");
        }
        else if (price <= 0) {
            presenter.presentFail("Price not loaded.");
        }
        else {
            final List<SubAccount> accounts = subAccountDao.getSubAccountsOf(username);
            SubAccount target = null;
            // Again, you have a lot of conditions being tested. You could refactor this code, and add all the
            // conditions to the same Chain of Responsibility design pattern.
            for (final SubAccount sa : accounts) {
                if (sa.getName().equalsIgnoreCase(portfolioName)) {
                    target = sa;
                    break;
                }
            }
            if (target == null) {
                presenter.presentFail("Portfolio not found.");
            }
            else {
                final BigDecimal cost = BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(qty));
                if (target.getBalanceUsd().compareTo(cost) < 0) {
                    presenter.presentFail("Insufficient funds.");
                }
                else {
                    // This code is very large, but is not doing much. It could definitely be improved. For example,
                    // the "stock" variable is only used once, so it could be made as an inline variable. Also, there
                    // are some "magic" strings, so you could add them to the Constants file, and use them here.
                    final BigDecimal newBal = target.getBalanceUsd().subtract(cost);
                    target.setBalanceUsd(newBal);
                    final Stock stock = new Stock(symbol, qty, symbol);
                    target.addOrIncreaseAsset(stock);
                    subAccountDao.save(username, target);
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
                    transactionDao.save(username,
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
            }
        }

    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis();
    }
}
