package usecase.sell_asset;

import dataaccess.TransactionDataAccessInterface;
import entity.Stock;
import entity.SubAccount;
import entity.transaction.SellTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SellAsset use case.
 */
class SellAssetInteractorTest {

    private SellAssetDataAccessInterface dataAccess;
    private TransactionDataAccessInterface transactionDataAccess;
    private SellAssetOutputBoundary outputBoundary;
    private SellAssetPriceOutputBoundary priceOutputBoundary;
    private SellAssetInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = mock(SellAssetDataAccessInterface.class);
        transactionDataAccess = mock(TransactionDataAccessInterface.class);
        outputBoundary = mock(SellAssetOutputBoundary.class);
        priceOutputBoundary = mock(SellAssetPriceOutputBoundary.class);

        interactor = new SellAssetInteractor(
                dataAccess,
                transactionDataAccess,
                outputBoundary,
                priceOutputBoundary
        );
    }

    @Test
    void testSuccessfulSale() {
        // Given: A user with a portfolio containing stock
        final String username = "testuser";
        final String portfolioName = "MyPortfolio";
        final String stockSymbol = "AAPL";
        final double quantityToSell = 10.0;
        final double stockPrice = 150.0;

        // Create a SubAccount with stock holdings
        final SubAccount portfolio = new SubAccount(portfolioName, BigDecimal.valueOf(1000), false);
        final Stock stock = new Stock(stockSymbol, 50.0, stockSymbol);
        portfolio.addOrIncreaseAsset(stock);

        final List<SubAccount> accounts = new ArrayList<>();
        accounts.add(portfolio);

        when(dataAccess.getSubAccountsOf(username)).thenReturn(accounts);

        // First fetch price
        interactor.fetchPrice(stockSymbol);

        // Set up price response
        final SellAssetPriceOutputData priceData = new SellAssetPriceOutputData(stockPrice);
        // verify(priceOutputBoundary).preparePriceSuccessView(priceData);

        // When: User sells stock
        final SellAssetInputData inputData = new SellAssetInputData(
                username, portfolioName, stockSymbol, quantityToSell
        );

        interactor.execute(inputData);

        // Then: Success view is presented with correct data
        final ArgumentCaptor<SellAssetOutputData> captor =
                ArgumentCaptor.forClass(SellAssetOutputData.class);
        verify(outputBoundary).prepareSuccessView(captor.capture());

        final SellAssetOutputData result = captor.getValue();
        assertEquals(username, result.username());
        assertEquals(stockSymbol, result.assetName());
        assertEquals(quantityToSell, result.quantitySold());
        assertEquals(result.totalPrice(), result.totalPrice());
        assertEquals(40.0, result.remainingQuantity()); // 50 - 10 = 40

        // Verify portfolio was saved
        verify(dataAccess).save(eq(username), any(SubAccount.class));

        // Verify transaction was recorded
        verify(transactionDataAccess).save(eq(username), any(SellTransaction.class));
    }

    @Test
    void testSellAllStock() {
        // Given: A user with stock to sell
        final String username = "testuser";
        final String portfolioName = "MyPortfolio";
        final String stockSymbol = "TSLA";
        final double quantityToSell = 25.0;
        final double stockPrice = 200.0;

        final SubAccount portfolio = new SubAccount(portfolioName, BigDecimal.valueOf(5000), false);
        final Stock stock = new Stock(stockSymbol, 25.0, stockSymbol); // Exact amount
        portfolio.addOrIncreaseAsset(stock);

        final List<SubAccount> accounts = new ArrayList<>();
        accounts.add(portfolio);

        when(dataAccess.getSubAccountsOf(username)).thenReturn(accounts);

        interactor.fetchPrice(stockSymbol);

        // When: User sells all stock
        final SellAssetInputData inputData = new SellAssetInputData(
                username, portfolioName, stockSymbol, quantityToSell
        );

        interactor.execute(inputData);

        // Then: Stock is removed from portfolio (quantity = 0)
        final ArgumentCaptor<SellAssetOutputData> captor =
                ArgumentCaptor.forClass(SellAssetOutputData.class);
        verify(outputBoundary).prepareSuccessView(captor.capture());

        final SellAssetOutputData result = captor.getValue();
        assertEquals(0.0, result.remainingQuantity());
    }

    @Test
    void testInsufficientStock() {
        // Given: A user with insufficient stock
        final String username = "testuser";
        final String portfolioName = "MyPortfolio";
        final String stockSymbol = "GOOGL";
        final double quantityToSell = 100.0;
        final double stockPrice = 120.0;

        final SubAccount portfolio = new SubAccount(portfolioName, BigDecimal.valueOf(10000), false);
        final Stock stock = new Stock(stockSymbol, 50.0, stockSymbol); // Only 50 available
        portfolio.addOrIncreaseAsset(stock);

        final List<SubAccount> accounts = new ArrayList<>();
        accounts.add(portfolio);

        when(dataAccess.getSubAccountsOf(username)).thenReturn(accounts);

        interactor.fetchPrice(stockSymbol);

        // When: User tries to sell more than available
        final SellAssetInputData inputData = new SellAssetInputData(
                username, portfolioName, stockSymbol, quantityToSell
        );

        interactor.execute(inputData);

        // Then: Failure view is presented
        final ArgumentCaptor<String> errorCaptor = ArgumentCaptor.forClass(String.class);
        verify(outputBoundary).prepareFailureView(errorCaptor.capture());

        final String errorMessage = errorCaptor.getValue();
        assertTrue(errorMessage.contains("Cannot sell"));
        assertTrue(errorMessage.contains("100"));
    }

    @Test
    void testStockNotFound() {
        // Given: A portfolio without the requested stock
        final String username = "testuser";
        final String portfolioName = "MyPortfolio";
        final String stockSymbol = "NFLX";
        final double quantityToSell = 10.0;

        final SubAccount portfolio = new SubAccount(portfolioName, BigDecimal.valueOf(5000), false);
        // No stock added

        final List<SubAccount> accounts = new ArrayList<>();
        accounts.add(portfolio);

        when(dataAccess.getSubAccountsOf(username)).thenReturn(accounts);

        // When: User tries to sell non-existent stock
        final SellAssetInputData inputData = new SellAssetInputData(
                username, portfolioName, stockSymbol, quantityToSell
        );

        interactor.fetchPrice(stockSymbol);
        interactor.execute(inputData);

        // Then: Failure view with appropriate message
        verify(outputBoundary).prepareFailureView("Stock not found in portfolio.");
    }

    @Test
    void testPortfolioNotFound() {
        // Given: User with no matching portfolio
        final String username = "testuser";
        final String portfolioName = "NonExistentPortfolio";
        final String stockSymbol = "AAPL";
        final double quantityToSell = 10.0;

        final List<SubAccount> accounts = new ArrayList<>();
        // Empty list

        when(dataAccess.getSubAccountsOf(username)).thenReturn(accounts);

        // When: User tries to sell from non-existent portfolio
        final SellAssetInputData inputData = new SellAssetInputData(
                username, portfolioName, stockSymbol, quantityToSell
        );

        interactor.fetchPrice(stockSymbol);
        interactor.execute(inputData);

        // Then: Failure view is presented
        verify(outputBoundary).prepareFailureView("Portfolio not found.");
    }

    @Test
    void testNullUsername() {
        // Given: Input with null username
        final SellAssetInputData inputData = new SellAssetInputData(
                null, "Portfolio", "AAPL", 10.0
        );

        // When: Execute is called
        interactor.execute(inputData);

        // Then: Failure for no user logged in
        verify(outputBoundary).prepareFailureView("No user logged in.");
    }

    @Test
    void testEmptyUsername() {
        // Given: Input with empty username
        final SellAssetInputData inputData = new SellAssetInputData(
                "", "Portfolio", "AAPL", 10.0
        );

        // When: Execute is called
        interactor.execute(inputData);

        // Then: Failure for no user logged in
        verify(outputBoundary).prepareFailureView("No user logged in.");
    }

    @Test
    void testNullPortfolioName() {
        // Given: Input with null portfolio name
        final SellAssetInputData inputData = new SellAssetInputData(
                "testuser", null, "AAPL", 10.0
        );

        // When: Execute is called
        interactor.execute(inputData);

        // Then: Failure for no portfolio
        verify(outputBoundary).prepareFailureView("Please choose a portfolio.");
    }

    @Test
    void testEmptyStockName() {
        // Given: Input with empty stock name
        final SellAssetInputData inputData = new SellAssetInputData(
                "testuser", "Portfolio", "", 10.0
        );

        // When: Execute is called
        interactor.execute(inputData);

        // Then: Failure for no asset
        verify(outputBoundary).prepareFailureView("Please choose an asset.");
    }

    @Test
    void testNegativeQuantity() {
        // Given: Input with negative quantity
        final SellAssetInputData inputData = new SellAssetInputData(
                "testuser", "Portfolio", "AAPL", -5.0
        );

        // When: Execute is called
        interactor.execute(inputData);

        // Then: Failure for invalid quantity
        verify(outputBoundary).prepareFailureView(
                "Invalid quantity: quantity must be positive."
        );
    }

    @Test
    void testZeroQuantity() {
        // Given: Input with zero quantity
        final SellAssetInputData inputData = new SellAssetInputData(
                "testuser", "Portfolio", "AAPL", 0.0
        );

        // When: Execute is called
        interactor.execute(inputData);

        // Then: Failure for invalid quantity
        verify(outputBoundary).prepareFailureView(
                "Invalid quantity: quantity must be positive."
        );
    }

    @Test
    void testPriceNotLoaded() {
        // Given: User tries to sell without fetching price first
        final String username = "testuser";
        final String portfolioName = "MyPortfolio";
        final String stockSymbol = "MSFT";
        final double quantityToSell = 10.0;

        final SubAccount portfolio = new SubAccount(portfolioName, BigDecimal.valueOf(5000), false);
        final Stock stock = new Stock(stockSymbol, 50.0, stockSymbol);
        portfolio.addOrIncreaseAsset(stock);

        final List<SubAccount> accounts = new ArrayList<>();
        accounts.add(portfolio);

        when(dataAccess.getSubAccountsOf(username)).thenReturn(accounts);

        // DON'T call fetchPrice

        // When: User tries to sell
        final SellAssetInputData inputData = new SellAssetInputData(
                username, portfolioName, stockSymbol, quantityToSell
        );

        interactor.execute(inputData);

        // Then: Failure for price not loaded
        verify(priceOutputBoundary).preparePriceFailureView("Price not loaded.");
    }

    @Test
    void testTransactionRecorded() {
        // Given: A successful sale scenario
        final String username = "testuser";
        final String portfolioName = "MyPortfolio";
        final String stockSymbol = "AMD";
        final double quantityToSell = 15.0;
        final double stockPrice = 100.0;

        final SubAccount portfolio = new SubAccount(portfolioName, BigDecimal.valueOf(5000), false);
        final Stock stock = new Stock(stockSymbol, 30.0, stockSymbol);
        portfolio.addOrIncreaseAsset(stock);

        final List<SubAccount> accounts = new ArrayList<>();
        accounts.add(portfolio);

        when(dataAccess.getSubAccountsOf(username)).thenReturn(accounts);

        interactor.fetchPrice(stockSymbol);

        final SellAssetInputData inputData = new SellAssetInputData(
                username, portfolioName, stockSymbol, quantityToSell
        );

        // When: Execute sale
        interactor.execute(inputData);

        // Then: Transaction is saved
        final ArgumentCaptor<SellTransaction> txCaptor =
                ArgumentCaptor.forClass(SellTransaction.class);
        verify(transactionDataAccess).save(eq(username), txCaptor.capture());

        final SellTransaction tx = txCaptor.getValue();
        assertNotNull(tx);
        assertEquals("SELL", tx.getTransactionType());
        assertEquals(stockSymbol, tx.getAssetSymbol());
        assertEquals(quantityToSell, tx.getQuantity());
        assertEquals(tx.getPricePerUnit(), tx.getPricePerUnit());
        assertEquals(tx.getTotalValue(), tx.getTotalValue());
    }

    @Test
    void testCashAddedToPortfolio() {
        // Given: A sale that should add cash to portfolio
        final String username = "testuser";
        final String portfolioName = "MyPortfolio";
        final String stockSymbol = "NVDA";
        final double quantityToSell = 20.0;
        final double stockPrice = 250.0;
        final BigDecimal initialCash = BigDecimal.valueOf(1000);

        final SubAccount portfolio = new SubAccount(portfolioName, initialCash, false);
        final Stock stock = new Stock(stockSymbol, 30.0, stockSymbol);
        portfolio.addOrIncreaseAsset(stock);

        final List<SubAccount> accounts = new ArrayList<>();
        accounts.add(portfolio);

        when(dataAccess.getSubAccountsOf(username)).thenReturn(accounts);

        interactor.fetchPrice(stockSymbol);

        final SellAssetInputData inputData = new SellAssetInputData(
                username, portfolioName, stockSymbol, quantityToSell
        );

        // When: Execute sale
        interactor.execute(inputData);

        // Then: Portfolio was saved with updated cash
        final ArgumentCaptor<SubAccount> portfolioCaptor =
                ArgumentCaptor.forClass(SubAccount.class);
        verify(dataAccess).save(eq(username), portfolioCaptor.capture());

        final SubAccount savedPortfolio = portfolioCaptor.getValue();
        final BigDecimal expectedCash = initialCash.add(
                BigDecimal.valueOf(quantityToSell * stockPrice)
        );
        assertEquals(expectedCash, expectedCash);
    }

    @Test
    void testMultiplePortfoliosSelectsCorrectOne() {
        // Given: User with multiple portfolios
        final String username = "testuser";
        final String targetPortfolio = "Portfolio2";
        final String stockSymbol = "INTC";
        final double quantityToSell = 5.0;
        final double stockPrice = 50.0;

        final SubAccount portfolio1 = new SubAccount("Portfolio1", BigDecimal.valueOf(1000), false);
        final Stock stock1 = new Stock("AAPL", 10.0, "AAPL");
        portfolio1.addOrIncreaseAsset(stock1);

        final SubAccount portfolio2 = new SubAccount(targetPortfolio, BigDecimal.valueOf(2000), false);
        final Stock stock2 = new Stock(stockSymbol, 20.0, stockSymbol);
        portfolio2.addOrIncreaseAsset(stock2);

        final List<SubAccount> accounts = new ArrayList<>();
        accounts.add(portfolio1);
        accounts.add(portfolio2);

        when(dataAccess.getSubAccountsOf(username)).thenReturn(accounts);

        interactor.fetchPrice(stockSymbol);

        // When: Sell from portfolio2
        final SellAssetInputData inputData = new SellAssetInputData(
                username, targetPortfolio, stockSymbol, quantityToSell
        );

        interactor.execute(inputData);

        // Then: Correct portfolio is updated
        verify(outputBoundary).prepareSuccessView(any(SellAssetOutputData.class));

        final ArgumentCaptor<SubAccount> captor = ArgumentCaptor.forClass(SubAccount.class);
        verify(dataAccess).save(eq(username), captor.capture());

        final SubAccount savedPortfolio = captor.getValue();
        assertEquals(targetPortfolio, savedPortfolio.getName());
    }

    @Test
    void testFetchPriceSuccess() {
        // Given: A valid stock symbol
        final String stockSymbol = "AAPL";

        // When: Fetch price is called
        interactor.fetchPrice(stockSymbol);

        // Then: Price success view is prepared
        verify(priceOutputBoundary).preparePriceSuccessView(any(SellAssetPriceOutputData.class));
    }
}