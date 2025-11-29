package usecase.buyasset;

import dataaccess.TransactionDataAccessInterface;
import entity.SubAccount;
import entity.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.SubAccount.SubAccountDataAccessInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuyAssetInteractorTest {

    private List<SubAccount> storedAccounts;
    private int txSaveCount;

    private SubAccountDataAccessInterface subAccountDAO;
    private TransactionDataAccessInterface transactionDAO;
    private TestBuyAssetPresenter presenter;

    private BuyAssetInputBoundary interactor;

    @BeforeEach
    void setUp() {
        storedAccounts = new ArrayList<>();
        txSaveCount = 0;

        subAccountDAO = new SubAccountDataAccessInterface() {
            @Override
            public List<SubAccount> getSubAccountsOf(String username) {
                return new ArrayList<>(storedAccounts);
            }

            @Override
            public int countByUser(String username) {
                return storedAccounts.size();
            }

            @Override
            public boolean exists(String username, String subName) {
                return storedAccounts.stream()
                        .anyMatch(a -> a.getName().equalsIgnoreCase(subName));
            }

            @Override
            public void save(String username, SubAccount subAccount) {
                storedAccounts.removeIf(a -> a.getName().equalsIgnoreCase(subAccount.getName()));
                storedAccounts.add(subAccount);
            }

            @Override
            public void delete(String username, String subName) {
                storedAccounts.removeIf(a -> a.getName().equalsIgnoreCase(subName));
            }
        };

        transactionDAO = new TransactionDataAccessInterface() {
            @Override
            public void save(String userId, Transaction transaction) {
                txSaveCount++;
            }

            @Override
            public List<Transaction> getByPortfolio(String userId, String portfolioId) {
                return List.of();
            }

            @Override
            public List<Transaction> getByFilters(String userId, String portfolioId, String assetSymbol,
                                                  LocalDate startDate, LocalDate endDate) {
                return List.of();
            }
        };

        presenter = new TestBuyAssetPresenter();

        interactor = new BuyAssetInteractor(subAccountDAO, transactionDAO, presenter);
    }

    //  SUCCESS CASE

    @Test
    void execute_successfulPurchase_updatesBalance_presentsSuccess() {
        final String username = "alice";
        final String portfolio = "Main";
        final String symbol = "AAPL";
        final int qty = 2;
        final double price = 100.0;

        SubAccount acc = new SubAccount(portfolio, new BigDecimal("1000.00"), false);
        storedAccounts.clear();
        storedAccounts.add(acc);

        BuyAssetInputData input = new BuyAssetInputData(
                username, portfolio, symbol, qty, price
        );

        interactor.execute(input);

        assertEquals(new BigDecimal("800.00"), acc.getBalanceUSD());
        assertTrue(presenter.successCalled);
        assertNull(presenter.failMessage);
        assertNotNull(presenter.lastOutputData);
        assertTrue(presenter.lastOutputData.message().contains("Purchased 2 of AAPL"));
    }

    //  FAILURE: INSUFFICIENT FUNDS

    @Test
    void execute_insufficientFunds_presentsFail_noBalanceChange_noTransaction() {
        final String username = "bob";
        final String portfolio = "Small";
        final String symbol = "GOOG";

        SubAccount acc = new SubAccount(portfolio, new BigDecimal("50.00"), false);
        storedAccounts.clear();
        storedAccounts.add(acc);

        BuyAssetInputData input = new BuyAssetInputData(
                username, portfolio, symbol, 1, 100.0
        );

        interactor.execute(input);

        assertEquals(new BigDecimal("50.00"), acc.getBalanceUSD());
        assertFalse(presenter.successCalled);
        assertEquals("Insufficient funds.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }

    //  FAILURE: INVALID QUANTITY

    @Test
    void execute_invalidQuantity_presentsFail_andDoesNotTouchDAOs() {
        final String username = "carol";
        final String portfolio = "Main";
        final String symbol = "MSFT";

        SubAccount acc = new SubAccount(portfolio, new BigDecimal("500.00"), false);
        storedAccounts.clear();
        storedAccounts.add(acc);

        BuyAssetInputData input = new BuyAssetInputData(
                username, portfolio, symbol, 0, 50.0
        );

        interactor.execute(input);

        assertEquals(new BigDecimal("500.00"), acc.getBalanceUSD());
        assertFalse(presenter.successCalled);
        assertEquals("Quantity must be positive.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }

    //  FAILURE: NO USER

    @Test
    void execute_noUserLoggedIn_presentsFail_andDoesNotTouchDAOs() {
        BuyAssetInputData input = new BuyAssetInputData(
                null, "Main", "AAPL", 1, 100.0
        );

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertEquals("No user logged in.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }

    //  FAILURE: NO PORTFOLIO

    @Test
    void execute_noPortfolioChosen_presentsFail_andDoesNotCallDAOs() {
        BuyAssetInputData input = new BuyAssetInputData(
                "alice", "", "AAPL", 1, 100.0
        );

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertEquals("Please choose a portfolio.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }

    //  FAILURE: NO SYMBOL

    @Test
    void execute_noSymbolChosen_presentsFail_andDoesNotCallDAOs() {
        BuyAssetInputData input = new BuyAssetInputData(
                "alice", "Main", "", 1, 100.0
        );

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertEquals("Please choose an asset.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }

    //  FAILURE: PRICE NOT LOADED

    @Test
    void execute_priceNotLoaded_presentsFail_andDoesNotCallDAOs() {
        BuyAssetInputData input = new BuyAssetInputData(
                "alice", "Main", "AAPL", 1, 0.0
        );

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }

    //  FAILURE: PORTFOLIO NOT FOUND

    @Test
    void execute_portfolioNotFound_presentsFail_andDoesNotSaveTransaction() {
        String username = "alice";

        SubAccount other = new SubAccount(
                "Other", new BigDecimal("1000.00"), false
        );
        storedAccounts.clear();
        storedAccounts.add(other);

        BuyAssetInputData input = new BuyAssetInputData(
                username, "Main", "AAPL", 1, 100.0
        );

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertEquals("Portfolio not found.", presenter.failMessage);
        assertEquals(new BigDecimal("1000.00"), other.getBalanceUSD());
        assertEquals(0, txSaveCount);
    }

    //  TEST PRESENTER

    private static class TestBuyAssetPresenter implements BuyAssetOutputBoundary {

        boolean successCalled = false;
        String failMessage = null;
        BuyAssetOutputData lastOutputData = null;

        @Override
        public void presentSuccess(BuyAssetOutputData outputData) {
            successCalled = true;
            lastOutputData = outputData;
        }

        @Override
        public void presentFail(String errorMessage) {
            failMessage = errorMessage;
        }
    }
    @Test
    void execute_emptyUsername_presentsFail_andDoesNotTouchDAOs() {
        BuyAssetInputData input = new BuyAssetInputData(
                "",
                "Main",
                "AAPL",
                1,
                100.0
        );

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertEquals("No user logged in.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }


    @Test
    void execute_nullPortfolioChosen_presentsFail_andDoesNotCallDAOs() {
        BuyAssetInputData input = new BuyAssetInputData(
                "alice",
                null,
                "AAPL",
                1,
                100.0
        );

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertEquals("Please choose a portfolio.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }


    @Test
    void execute_nullSymbolChosen_presentsFail_andDoesNotCallDAOs() {
        BuyAssetInputData input = new BuyAssetInputData(
                "alice",
                "Main",
                null,
                1,
                100.0
        );

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertEquals("Please choose an asset.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }

    @Test
    void execute_negativeQuantity_presentsFail_andDoesNotTouchDAOs() {
        SubAccount acc = new SubAccount("Main", new BigDecimal("500.00"), false);
        storedAccounts.clear();
        storedAccounts.add(acc);

        BuyAssetInputData input = new BuyAssetInputData(
                "alice",
                "Main",
                "AAPL",
                -1,
                50.0
        );

        interactor.execute(input);

        assertEquals(new BigDecimal("500.00"), acc.getBalanceUSD());
        assertFalse(presenter.successCalled);
        assertEquals("Quantity must be positive.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }

    @Test
    void execute_negativePrice_presentsFail_andDoesNotCallDAOs() {
        BuyAssetInputData input = new BuyAssetInputData(
                "alice",
                "Main",
                "AAPL",
                1,
                -1.0
        );

        interactor.execute(input);

        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
        assertEquals(0, txSaveCount);
    }

}
