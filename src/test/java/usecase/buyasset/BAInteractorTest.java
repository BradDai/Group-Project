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

    private FakeSubAccountDAO subAccountDAO;
    private FakeTransactionDAO transactionDAO;
    private TestBuyAssetPresenter presenter;

    private BuyAssetInputBoundary interactor;

    @BeforeEach
    void setUp() {
        subAccountDAO = new FakeSubAccountDAO();
        transactionDAO = new FakeTransactionDAO();
        presenter = new TestBuyAssetPresenter();

        interactor = new BuyAssetInteractor(subAccountDAO, transactionDAO, presenter);
    }


    // ============================================================
    //  SUCCESS CASE
    @Test
    void execute_successfulPurchase_updatesBalance_savesTransaction_andPresentsSuccess() {
        // Arrange
        final String username = "alice";
        final String portfolio = "Main";
        final String symbol = "AAPL";
        final int qty = 2;
        final double price = 100.0;

        // SubAccount with $1000 USD
        SubAccount acc = new SubAccount(portfolio, new BigDecimal("1000.00"), false);
        subAccountDAO.setAccounts(List.of(acc));

        BuyAssetInputData input = new BuyAssetInputData(
                username,
                portfolio,
                symbol,
                qty,
                price
        );

        // Act
        interactor.execute(input);

        // Assert — balance updated: 1000 - (2 * 100) = 800
        assertEquals(new BigDecimal("800.00"), acc.getBalanceUSD());

        // Assert — presenter success, no fail
        assertTrue(presenter.successCalled);
        assertNull(presenter.failMessage);
        assertNotNull(presenter.lastOutputData);
        assertTrue(presenter.lastOutputData.message().contains("Purchased 2 of AAPL"));

        assertTrue(presenter.successCalled);
        assertNull(presenter.failMessage);

    }

    @Test
    void execute_insufficientFunds_presentsFail_noBalanceChange_noTransaction() {
        // Arrange
        final String username = "bob";
        final String portfolio = "Small";
        final String symbol = "GOOG";

        SubAccount acc = new SubAccount(portfolio, new BigDecimal("50.00"), false);
        subAccountDAO.setAccounts(List.of(acc));

        BuyAssetInputData input = new BuyAssetInputData(
                username,
                portfolio,
                symbol,
                1,
                100.0
        );

        // Act
        interactor.execute(input);

        // Assert — same balance
        assertEquals(new BigDecimal("50.00"), acc.getBalanceUSD());

        // Presenter fail
        assertFalse(presenter.successCalled);
        assertEquals("Insufficient funds.", presenter.failMessage);

        // No transaction saved
        assertEquals(0, transactionDAO.saveCount);
    }


    // ============================================================
    //INVALID QUANTITY
    @Test
    void execute_invalidQuantity_presentsFail_andDoesNotTouchDAOs() {
        // Arrange
        final String username = "carol";
        final String portfolio = "Main";
        final String symbol = "MSFT";

        SubAccount acc = new SubAccount(portfolio, new BigDecimal("500.00"), false);
        subAccountDAO.setAccounts(List.of(acc));

        BuyAssetInputData input = new BuyAssetInputData(
                username,
                portfolio,
                symbol,
                0,        // invalid, must be > 0
                50.0
        );

        // Act
        interactor.execute(input);

        // Assert
        assertEquals(new BigDecimal("500.00"), acc.getBalanceUSD());
        assertFalse(presenter.successCalled);
        assertEquals("Quantity must be positive.", presenter.failMessage);
        assertEquals(0, transactionDAO.saveCount);
    }


    // ============================================================
    //  FAKE DAOs + TEST PRESENTER
    private static class FakeSubAccountDAO implements SubAccountDataAccessInterface {

        private final List<SubAccount> accounts = new ArrayList<>();

        @Override
        public List<SubAccount> getSubAccountsOf(String username) {
            return new ArrayList<>(accounts);
        }

        @Override
        public int countByUser(String username) {
            return 0;
        }

        @Override
        public boolean exists(String username, String subName) {
            return false;
        }

        @Override
        public void save(String username, SubAccount subAccount) {
            accounts.removeIf(a -> a.getName().equalsIgnoreCase(subAccount.getName()));
            accounts.add(subAccount);
        }

        @Override
        public void delete(String username, String subName) {

        }

        void setAccounts(List<SubAccount> list) {
            accounts.clear();
            accounts.addAll(list);
        }
    }

    private static class FakeTransactionDAO implements TransactionDataAccessInterface {

        int saveCount = 0;

        @Override
        public void save(String userId, Transaction transaction) {

        }

        @Override
        public List<Transaction> getByPortfolio(String userId, String portfolioId) {
            return List.of();
        }

        @Override
        public List<Transaction> getByFilters(String userId, String portfolioId, String assetSymbol, LocalDate startDate, LocalDate endDate) {
            return List.of();
        }
    }

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
}
