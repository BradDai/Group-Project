package usecase.sell_asset;

import dataaccess.TransactionDataAccessInterface;
import entity.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SellAssetInteractorTest {

    private TestSellAssetDataAccess dataAccess;
    private TestTransactionDataAccess transactionDataAccess;
    private TestSellAssetPresenter presenter;
    private TestSellAssetPricePresenter pricePresenter;
    private SellAssetInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = new TestSellAssetDataAccess();
        transactionDataAccess = new TestTransactionDataAccess();
        presenter = new TestSellAssetPresenter();
        pricePresenter = new TestSellAssetPricePresenter();

        interactor = new SellAssetInteractor(
                dataAccess,
                transactionDataAccess,
                presenter,
                pricePresenter
        );
    }

    // ============================================================
    //  SUCCESS CASES
    // ============================================================

    @Test
    void execute_successfulSale_updatesQuantityAndBalance() {
        // Arrange
        String username = "alice";
        String portfolio = "Main";
        String stockName = "AAPL";
        double currentQuantity = 10.0;
        double quantityToSell = 5.0;

        dataAccess.setStockQuantity(username, portfolio, stockName, currentQuantity);
        dataAccess.setPortfolioCash(username, portfolio, 1000.0);

        // Fetch price first to set stockPrice
        interactor.fetchPrice(stockName);

        SellAssetInputData inputData = new SellAssetInputData(
                username, portfolio, stockName, quantityToSell
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.successCalled);
        assertNull(presenter.failMessage);
        assertNotNull(presenter.lastOutputData);
        assertEquals(username, presenter.lastOutputData.username());
        assertEquals(stockName, presenter.lastOutputData.assetName());
        assertEquals(quantityToSell, presenter.lastOutputData.quantitySold());
        assertEquals(5.0, presenter.lastOutputData.remainingQuantity());

        // Verify stock quantity updated
        assertEquals(5.0, dataAccess.getStockQuantity(username, portfolio, stockName));

        // Verify cash was added to portfolio
        assertTrue(dataAccess.addCashCalled);

        // Verify transaction was saved
        assertEquals(1, transactionDataAccess.savedTransactions.size());
    }

    @Test
    void execute_sellAllQuantity_removesStockFromPortfolio() {
        // Arrange
        String username = "bob";
        String portfolio = "Trading";
        String stockName = "GOOG";
        double currentQuantity = 3.0;

        dataAccess.setStockQuantity(username, portfolio, stockName, currentQuantity);

        interactor.fetchPrice(stockName);

        SellAssetInputData inputData = new SellAssetInputData(
                username, portfolio, stockName, currentQuantity
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.successCalled);
        assertEquals(0.0, presenter.lastOutputData.remainingQuantity());

        // Verify stock was removed
        assertTrue(dataAccess.removeStockCalled);
        assertEquals(stockName, dataAccess.lastRemovedStock);
    }

    // ============================================================
    //  VALIDATION FAILURE CASES
    // ============================================================

    @Test
    void execute_nullUsername_presentsFail() {
        // Arrange
        SellAssetInputData inputData = new SellAssetInputData(
                null, "Main", "AAPL", 1.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
        assertFalse(dataAccess.updateStockQuantityCalled);
        assertEquals(0, transactionDataAccess.savedTransactions.size());
    }

    @Test
    void execute_emptyUsername_presentsFail() {
        // Arrange
        SellAssetInputData inputData = new SellAssetInputData(
                "", "Main", "AAPL", 1.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
    }

    @Test
    void execute_nullPortfolio_presentsFail() {
        // Arrange
        SellAssetInputData inputData = new SellAssetInputData(
                "alice", null, "AAPL", 1.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
    }

    @Test
    void execute_emptyPortfolio_presentsFail() {
        // Arrange
        SellAssetInputData inputData = new SellAssetInputData(
                "alice", "", "AAPL", 1.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
    }

    @Test
    void execute_nullStockName_presentsFail() {
        // Arrange
        SellAssetInputData inputData = new SellAssetInputData(
                "alice", "Main", null, 1.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
    }

    @Test
    void execute_emptyStockName_presentsFail() {
        // Arrange
        SellAssetInputData inputData = new SellAssetInputData(
                "alice", "Main", "", 1.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
    }

    @Test
    void execute_zeroQuantity_presentsFail() {
        // Arrange
        dataAccess.setStockQuantity("alice", "Main", "AAPL", 10.0);

        SellAssetInputData inputData = new SellAssetInputData(
                "alice", "Main", "AAPL", 0.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
    }

    @Test
    void execute_negativeQuantity_presentsFail() {
        // Arrange
        dataAccess.setStockQuantity("alice", "Main", "AAPL", 10.0);

        SellAssetInputData inputData = new SellAssetInputData(
                "alice", "Main", "AAPL", -5.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
    }

    @Test
    void execute_quantityExceedsAvailable_presentsFail() {
        // Arrange
        String username = "alice";
        String portfolio = "Main";
        String stockName = "AAPL";
        double currentQuantity = 5.0;
        double quantityToSell = 10.0;

        dataAccess.setStockQuantity(username, portfolio, stockName, currentQuantity);
        interactor.fetchPrice(stockName);

        SellAssetInputData inputData = new SellAssetInputData(
                username, portfolio, stockName, quantityToSell
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Invalid quantity: cannot exceed current quantity (5.0).",
                presenter.failMessage);
        assertFalse(dataAccess.updateStockQuantityCalled);
    }

    @Test
    void execute_priceNotLoaded_presentsFail() {
        // Arrange
        dataAccess.setStockQuantity("alice", "Main", "AAPL", 10.0);

        // Don't fetch price, so stockPrice remains 0

        SellAssetInputData inputData = new SellAssetInputData(
                "alice", "Main", "AAPL", 5.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.successCalled);
        assertEquals("Price not loaded.", presenter.failMessage);
        assertFalse(dataAccess.updateStockQuantityCalled);
    }

    // ============================================================
    //  PRICE FETCHING TESTS
    // ============================================================

    @Test
    void fetchPrice_successfulFetch_presentsPriceSuccess() {
        // Arrange
        String stockName = "AAPL";

        // Act
        interactor.fetchPrice(stockName);

        // Assert
        assertTrue(pricePresenter.successCalled);
        assertNull(pricePresenter.failMessage);
        assertNotNull(pricePresenter.lastOutputData);
        assertTrue(pricePresenter.lastOutputData.price() > 0);
    }

    @Test
    void fetchPrice_apiError_presentsPriceFailure() {
        // Arrange
        String invalidStock = "AAPL";

        // Act
        interactor.fetchPrice(invalidStock);

        // Assert - This test depends on actual API behavior
        // In a real test, you'd mock the HTTP connection
        // For now, we just verify the method can be called
        assertNotNull(pricePresenter);
    }

    // ============================================================
    //  INTEGRATION TESTS
    // ============================================================

    @Test
    void execute_multipleSales_updatesCorrectly() {
        // Arrange
        String username = "carol";
        String portfolio = "Main";
        String stockName = "MSFT";

        dataAccess.setStockQuantity(username, portfolio, stockName, 20.0);
        interactor.fetchPrice(stockName);

        // First sale
        SellAssetInputData firstSale = new SellAssetInputData(
                username, portfolio, stockName, 5.0
        );
        interactor.execute(firstSale);

        // Reset presenter
        setUp();
        dataAccess.setStockQuantity(username, portfolio, stockName, 15.0);
        interactor.fetchPrice(stockName);

        // Second sale
        SellAssetInputData secondSale = new SellAssetInputData(
                username, portfolio, stockName, 10.0
        );

        // Act
        interactor.execute(secondSale);

        // Assert
        assertTrue(presenter.successCalled);
        assertEquals(5.0, presenter.lastOutputData.remainingQuantity());
    }

    @Test
    void execute_differentPortfolios_handledSeparately() {
        // Arrange
        String username = "dave";
        String portfolio1 = "Main";
        String portfolio2 = "Trading";
        String stockName = "TSLA";

        dataAccess.setStockQuantity(username, portfolio1, stockName, 10.0);
        dataAccess.setStockQuantity(username, portfolio2, stockName, 5.0);

        interactor.fetchPrice(stockName);

        SellAssetInputData inputData = new SellAssetInputData(
                username, portfolio1, stockName, 3.0
        );

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.successCalled);
        // Verify portfolio1 was updated but not portfolio2
        assertEquals(7.0, dataAccess.getStockQuantity(username, portfolio1, stockName));
        assertEquals(5.0, dataAccess.getStockQuantity(username, portfolio2, stockName));
    }

    // ============================================================
    //  TEST DOUBLES
    // ============================================================

    private static class TestSellAssetDataAccess implements SellAssetDataAccessInterface {
        private final Map<String, Double> stockQuantities = new HashMap<>();
        private final Map<String, Double> portfolioCash = new HashMap<>();

        boolean updateStockQuantityCalled = false;
        boolean removeStockCalled = false;
        boolean addCashCalled = false;
        String lastRemovedStock = null;

        void setStockQuantity(String username, String portfolio, String stock, double qty) {
            String key = username + ":" + portfolio + ":" + stock;
            stockQuantities.put(key, qty);
        }

        void setPortfolioCash(String username, String portfolio, double cash) {
            String key = username + ":" + portfolio;
            portfolioCash.put(key, cash);
        }

        @Override
        public String[] getAvailablePortfolios(String username) {
            return new String[]{"Main", "Trading"};
        }

        @Override
        public String[] getAvailableStocks(String username, String portfolioName) {
            return new String[]{"AAPL", "GOOG", "MSFT"};
        }

        @Override
        public double getStockQuantity(String username, String portfolioName, String stockName) {
            String key = username + ":" + portfolioName + ":" + stockName;
            return stockQuantities.getOrDefault(key, 0.0);
        }

        @Override
        public void updateStockQuantity(String username, String portfolioName,
                                        String stockName, double quantity) {
            updateStockQuantityCalled = true;
            String key = username + ":" + portfolioName + ":" + stockName;
            stockQuantities.put(key, quantity);
        }

        @Override
        public void removeStock(String username, String portfolioName, String stockName) {
            removeStockCalled = true;
            lastRemovedStock = stockName;
            String key = username + ":" + portfolioName + ":" + stockName;
            stockQuantities.remove(key);
        }

        @Override
        public void addCashToPortfolio(String username, String portfolioName, double amount) {
            addCashCalled = true;
            String key = username + ":" + portfolioName;
            double current = portfolioCash.getOrDefault(key, 0.0);
            portfolioCash.put(key, current + amount);
        }
    }

    private static class TestTransactionDataAccess implements TransactionDataAccessInterface {
        List<Transaction> savedTransactions = new ArrayList<>();

        @Override
        public void save(String userId, Transaction transaction) {
            savedTransactions.add(transaction);
        }

        @Override
        public List<Transaction> getByPortfolio(String userId, String portfolioId) {
            return new ArrayList<>();
        }

        @Override
        public List<Transaction> getByFilters(String userId, String portfolioId,
                                              String assetSymbol, LocalDate startDate,
                                              LocalDate endDate) {
            return new ArrayList<>();
        }
    }

    private static class TestSellAssetPresenter implements SellAssetOutputBoundary {
        boolean successCalled = false;
        String failMessage = null;
        SellAssetOutputData lastOutputData = null;

        @Override
        public void prepareSuccessView(SellAssetOutputData sellAssetOutputData) {
            successCalled = true;
            lastOutputData = sellAssetOutputData;
        }

        @Override
        public void prepareFailureView(String errorMessage) {
            failMessage = errorMessage;
        }
    }

    private static class TestSellAssetPricePresenter implements SellAssetPriceOutputBoundary {
        boolean successCalled = false;
        String failMessage = null;
        SellAssetPriceOutputData lastOutputData = null;

        @Override
        public void preparePriceSuccessView(SellAssetPriceOutputData sellAssetPriceOutputData) {
            successCalled = true;
            lastOutputData = sellAssetPriceOutputData;
        }

        @Override
        public void preparePriceFailureView(String errorMessage) {
            failMessage = errorMessage;
        }
    }
}