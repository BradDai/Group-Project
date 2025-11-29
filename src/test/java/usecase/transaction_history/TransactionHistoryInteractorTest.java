
package usecase.transaction_history;

import dataaccess.TransactionDataAccessInterface;
import entity.transaction.*;
import interfaceadapter.history.HistoryState;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionHistoryInteractorTest {

    private TransactionDataAccessInterface mockTransactionRepo;
    private TransactionHistoryOutputBoundary mockPresenter;
    private LoggedInViewModel mockLoggedInViewModel;
    private LoggedInState mockLoggedInState;
    private TransactionHistoryInteractor interactor;

    @BeforeEach
    void setUp() {
        mockTransactionRepo = mock(TransactionDataAccessInterface.class);
        mockPresenter = mock(TransactionHistoryOutputBoundary.class);
        mockLoggedInViewModel = mock(LoggedInViewModel.class);
        mockLoggedInState = mock(LoggedInState.class);

        when(mockLoggedInViewModel.getState()).thenReturn(mockLoggedInState);
        when(mockLoggedInState.getUsername()).thenReturn("testuser");

        interactor = new TransactionHistoryInteractor(
                mockTransactionRepo,
                mockPresenter,
                mockLoggedInViewModel
        );
    }

    @Test
    @DisplayName("TH-001: Retrieve All Transactions for a Portfolio")
    void testRetrieveAllTransactionsForPortfolio() {
        String portfolio = "Portfolio1";
        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                portfolio, null, null, null
        );

        List<Transaction> mockTransactions = createMockTransactions();
        when(mockTransactionRepo.getByFilters(
                eq("testuser"), eq(portfolio), isNull(), isNull(), isNull()
        )).thenReturn(mockTransactions);

        interactor.execute(inputData);

        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        TransactionHistoryOutputData output = captor.getValue();
        assertEquals(mockTransactions.size(), output.rows().size());
        assertTrue(output.message().contains("Loaded " + mockTransactions.size()));
        assertTrue(output.message().contains("portfolio Portfolio1"));
    }

    @Test
    @DisplayName("TH-002: Filter Transactions by Asset")
    void testFilterTransactionsByAsset() {
        String asset = "BTC";
        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", asset, null, null
        );

        BuyTransaction btcTransaction = createBuyTransaction("BTC", 100.0, 5000.0);
        List<Transaction> mockTransactions = List.of(btcTransaction);

        when(mockTransactionRepo.getByFilters(
                eq("testuser"), eq("Portfolio1"), eq(asset), isNull(), isNull()
        )).thenReturn(mockTransactions);

        interactor.execute(inputData);

        verify(mockTransactionRepo).getByFilters("testuser", "Portfolio1", asset, null, null);
        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        TransactionHistoryOutputData output = captor.getValue();
        assertEquals(1, output.rows().size());
        assertEquals("BTC", output.rows().get(0).asset);
    }

    @Test
    @DisplayName("TH-003: Filter Transactions by Date Range")
    void testFilterTransactionsByDateRange() {
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", null, startDate, endDate
        );

        List<Transaction> mockTransactions = createMockTransactions();
        when(mockTransactionRepo.getByFilters(
                eq("testuser"),
                eq("Portfolio1"),
                isNull(),
                eq(LocalDate.parse(startDate)),
                eq(LocalDate.parse(endDate))
        )).thenReturn(mockTransactions);

        interactor.execute(inputData);

        verify(mockTransactionRepo).getByFilters(
                "testuser",
                "Portfolio1",
                null,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );

        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        TransactionHistoryOutputData output = captor.getValue();
        assertTrue(output.message().contains("from 2024-01-01 to 2024-12-31"));
    }

    // NEW TEST #1 – only start date, no end date
    @Test
    @DisplayName("TH-013: Only start date provided (no end date)")
    void testOnlyStartDateProvided_noEndDate() {
        String startDate = "2024-04-01";
        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", null, startDate, null
        );

        List<Transaction> mockTransactions = createMockTransactions();
        when(mockTransactionRepo.getByFilters(
                eq("testuser"),
                eq("Portfolio1"),
                isNull(),
                eq(LocalDate.parse(startDate)),
                isNull()
        )).thenReturn(mockTransactions);

        interactor.execute(inputData);

        // Verify DAO call uses start date but null end date
        verify(mockTransactionRepo).getByFilters(
                "testuser",
                "Portfolio1",
                null,
                LocalDate.parse(startDate),
                null
        );

        // Message should NOT contain "from ... to ..." because end is null
        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        TransactionHistoryOutputData output = captor.getValue();
        assertTrue(output.message().contains("Loaded " + mockTransactions.size() + " transactions"));
        assertFalse(output.message().contains("from " + startDate));
    }

    @Test
    @DisplayName("TH-004: Handle BuyTransaction Type")
    void testHandleBuyTransaction() {
        BuyTransaction buyTx = createBuyTransaction("ETH", 50.0, 3000.0);
        when(mockTransactionRepo.getByFilters(anyString(), anyString(), any(), any(), any()))
                .thenReturn(List.of(buyTx));

        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", null, null, null
        );

        interactor.execute(inputData);

        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        HistoryState.Row row = captor.getValue().rows().get(0);
        assertEquals("ETH", row.asset);
        assertEquals("BUY", row.type);
        assertEquals(50.0, row.quantity);
        assertEquals(3000.0, row.totalValue);
    }

    @Test
    @DisplayName("TH-005: Handle SellTransaction Type")
    void testHandleSellTransaction() {
        SellTransaction sellTx = createSellTransaction("BTC", 25.0, 2500.0);
        when(mockTransactionRepo.getByFilters(anyString(), anyString(), any(), any(), any()))
                .thenReturn(List.of(sellTx));

        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", null, null, null
        );

        interactor.execute(inputData);

        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        HistoryState.Row row = captor.getValue().rows().get(0);
        assertEquals("BTC", row.asset);
        assertEquals("SELL", row.type);
        assertEquals(25.0, row.quantity);
        assertEquals(2500.0, row.totalValue);
    }

    @Test
    @DisplayName("TH-006: Handle ConvertTransaction Type")
    void testHandleConvertTransaction() {
        ConvertTransaction convertTx = createConvertTransaction("USD", "EUR", 1000.0, 850.0);
        when(mockTransactionRepo.getByFilters(anyString(), anyString(), any(), any(), any()))
                .thenReturn(List.of(convertTx));

        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", null, null, null
        );

        interactor.execute(inputData);

        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        HistoryState.Row row = captor.getValue().rows().get(0);
        assertEquals("USD->EUR", row.asset);
        assertEquals("CONVERT", row.type);
        assertEquals(1000.0, row.quantity);
        assertEquals(850.0, row.totalValue);
    }

    @Test
    @DisplayName("TH-007: Handle TransferTransaction Type")
    void testHandleTransferTransaction() {
        TransferTransaction transferTx = createTransferTransaction("BTC", 10.0);
        when(mockTransactionRepo.getByFilters(anyString(), anyString(), any(), any(), any()))
                .thenReturn(List.of(transferTx));

        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", null, null, null
        );

        interactor.execute(inputData);

        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        HistoryState.Row row = captor.getValue().rows().get(0);
        assertEquals("BTC", row.asset);
        assertEquals("TRANSFER", row.type);
        assertEquals(10.0, row.quantity);
        assertEquals(0.0, row.totalValue);
    }

    @Test
    @DisplayName("TH-008: Handle Unknown Transaction Type")
    void testHandleUnknownTransactionType() {
        Transaction unknownTx = mock(Transaction.class);
        when(unknownTx.getTransactionId()).thenReturn("tx-999");
        when(unknownTx.getDate()).thenReturn(LocalDateTime.now());
        when(unknownTx.getTransactionType()).thenReturn("UNKNOWN");

        when(mockTransactionRepo.getByFilters(anyString(), anyString(), any(), any(), any()))
                .thenReturn(List.of(unknownTx));

        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", null, null, null
        );

        interactor.execute(inputData);

        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        HistoryState.Row row = captor.getValue().rows().get(0);
        assertEquals("", row.asset);
        assertEquals("UNKNOWN", row.type);
        assertEquals(0.0, row.quantity);
        assertEquals(0.0, row.totalValue);
    }

    @Test
    @DisplayName("TH-009: Empty Result Set")
    void testEmptyResultSet() {
        when(mockTransactionRepo.getByFilters(anyString(), anyString(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "EmptyPortfolio", null, null, null
        );

        interactor.execute(inputData);

        ArgumentCaptor<TransactionHistoryOutputData> captor =
                ArgumentCaptor.forClass(TransactionHistoryOutputData.class);
        verify(mockPresenter).present(captor.capture());

        TransactionHistoryOutputData output = captor.getValue();
        assertTrue(output.rows().isEmpty());
        assertTrue(output.message().contains("Loaded 0 transactions"));
    }

    @Test
    @DisplayName("TH-010: Parse Valid Date Strings")
    void testParseValidDateStrings() {
        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", null, "2024-03-15", "2024-03-20"
        );

        when(mockTransactionRepo.getByFilters(
                anyString(), anyString(), any(),
                eq(LocalDate.of(2024, 3, 15)),
                eq(LocalDate.of(2024, 3, 20))
        )).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> interactor.execute(inputData));

        verify(mockTransactionRepo).getByFilters(
                anyString(), anyString(), any(),
                eq(LocalDate.of(2024, 3, 15)),
                eq(LocalDate.of(2024, 3, 20))
        );
    }

    @Test
    @DisplayName("TH-011: Parse Null Date String")
    void testParseNullDateString() {
        TransactionHistoryInputData inputData = new TransactionHistoryInputData(
                "Portfolio1", null, null, null
        );

        when(mockTransactionRepo.getByFilters(
                anyString(), anyString(), any(), isNull(), isNull()
        )).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> interactor.execute(inputData));

        verify(mockTransactionRepo).getByFilters(
                anyString(), anyString(), any(), isNull(), isNull()
        );
    }

    @Test
    @DisplayName("TH-012: Parse Empty/Blank Date String")
    void testParseEmptyBlankDateString() {
        TransactionHistoryInputData inputData1 = new TransactionHistoryInputData(
                "Portfolio1", null, "", "   "
        );

        when(mockTransactionRepo.getByFilters(
                anyString(), anyString(), any(), isNull(), isNull()
        )).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> interactor.execute(inputData1));

        verify(mockTransactionRepo).getByFilters(
                anyString(), anyString(), any(), isNull(), isNull()
        );
    }

    @Test
    @DisplayName("TH-014: Load Portfolio Options Successfully")
    void testLoadPortfolioOptionsSuccessfully() {
        BuyTransaction tx1 = createBuyTransactionWithPortfolio("tx1", "Portfolio1", "Portfolio2");
        SellTransaction tx2 = createSellTransactionWithPortfolio("tx2", "Portfolio2", "Portfolio3");

        when(mockTransactionRepo.getByFilters(
                eq("testuser"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(Arrays.asList(tx1, tx2));

        interactor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());

        ArrayList<String> portfolios = captor.getValue();
        assertEquals(3, portfolios.size());
        assertTrue(portfolios.contains("Portfolio1"));
        assertTrue(portfolios.contains("Portfolio2"));
        assertTrue(portfolios.contains("Portfolio3"));
    }

    @Test
    @DisplayName("TH-015: Load Portfolios from Both Source and Destination")
    void testLoadPortfoliosFromBothSourceAndDestination() {
        TransferTransaction tx = createTransferTransactionWithPortfolios("A", "B");

        when(mockTransactionRepo.getByFilters(
                eq("testuser"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(List.of(tx));

        interactor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());

        ArrayList<String> portfolios = captor.getValue();
        assertTrue(portfolios.contains("A"));
        assertTrue(portfolios.contains("B"));
    }

    @Test
    @DisplayName("TH-016: Handle Null Portfolio Names")
    void testHandleNullPortfolioNames() {
        Transaction tx = mock(Transaction.class);
        when(tx.getFromPortfolio()).thenReturn(null);
        when(tx.getToPortfolio()).thenReturn("ValidPortfolio");

        when(mockTransactionRepo.getByFilters(
                eq("testuser"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(List.of(tx));

        interactor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());

        ArrayList<String> portfolios = captor.getValue();
        assertEquals(1, portfolios.size());
        assertEquals("ValidPortfolio", portfolios.get(0));
    }

    @Test
    @DisplayName("TH-017: Handle Blank Portfolio Names")
    void testHandleBlankPortfolioNames() {
        Transaction tx = mock(Transaction.class);
        when(tx.getFromPortfolio()).thenReturn("   ");
        when(tx.getToPortfolio()).thenReturn("ValidPortfolio");

        when(mockTransactionRepo.getByFilters(
                eq("testuser"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(List.of(tx));

        interactor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());

        ArrayList<String> portfolios = captor.getValue();
        assertEquals(1, portfolios.size());
        assertEquals("ValidPortfolio", portfolios.get(0));
    }

    @Test
    @DisplayName("TH-018: Load Options with Null LoggedInViewModel")
    void testLoadOptionsWithNullViewModel() {
        TransactionHistoryInteractor nullViewModelInteractor = new TransactionHistoryInteractor(
                mockTransactionRepo,
                mockPresenter,
                null
        );

        nullViewModelInteractor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }

    @Test
    @DisplayName("TH-019: Load Options with Null ViewModel State")
    void testLoadOptionsWithNullViewModelState() {
        when(mockLoggedInViewModel.getState()).thenReturn(null);

        interactor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }

    @Test
    @DisplayName("TH-020: Load Options with Null Username")
    void testLoadOptionsWithNullUsername() {
        when(mockLoggedInState.getUsername()).thenReturn(null);

        interactor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());
        assertTrue(captor.getValue().isEmpty());
        verify(mockTransactionRepo, never()).getByFilters(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("TH-021: Load Options with Blank Username")
    void testLoadOptionsWithBlankUsername() {
        when(mockLoggedInState.getUsername()).thenReturn("   ");

        interactor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());
        assertTrue(captor.getValue().isEmpty());
        verify(mockTransactionRepo, never()).getByFilters(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("TH-022: Load Options with No Transactions")
    void testLoadOptionsWithNoTransactions() {
        when(mockTransactionRepo.getByFilters(
                eq("testuser"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(new ArrayList<>());

        interactor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }

    // NEW TEST #2 – cover branches where toPortfolio is null / blank
    @Test
    @DisplayName("TH-023: Ignore null and blank 'to' portfolio names")
    void testIgnoreNullAndBlankToPortfolios() {
        Transaction tx1 = mock(Transaction.class);
        when(tx1.getFromPortfolio()).thenReturn("From1");
        when(tx1.getToPortfolio()).thenReturn(null);   // should be ignored

        Transaction tx2 = mock(Transaction.class);
        when(tx2.getFromPortfolio()).thenReturn("From2");
        when(tx2.getToPortfolio()).thenReturn("   ");  // also ignored

        when(mockTransactionRepo.getByFilters(
                eq("testuser"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(List.of(tx1, tx2));

        interactor.loadPortfolioOptions();

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mockPresenter).presentPortfolioOptions(captor.capture());

        ArrayList<String> portfolios = captor.getValue();
        // Only the non-blank 'from' portfolios should be present
        assertEquals(2, portfolios.size());
        assertTrue(portfolios.contains("From1"));
        assertTrue(portfolios.contains("From2"));
    }

    // ===== Helper methods =====

    private List<Transaction> createMockTransactions() {
        return Arrays.asList(
                createBuyTransaction("BTC", 100.0, 5000.0),
                createSellTransaction("ETH", 50.0, 3000.0)
        );
    }

    private BuyTransaction createBuyTransaction(String asset, double quantity, double totalValue) {
        BuyTransaction tx = mock(BuyTransaction.class);
        when(tx.getTransactionId()).thenReturn("buy-" + asset);
        when(tx.getDate()).thenReturn(LocalDateTime.now());
        when(tx.getAssetSymbol()).thenReturn(asset);
        when(tx.getQuantity()).thenReturn(quantity);
        when(tx.getTotalValue()).thenReturn(totalValue);
        when(tx.getTransactionType()).thenReturn("BUY");
        return tx;
    }

    private BuyTransaction createBuyTransactionWithPortfolio(String id, String from, String to) {
        BuyTransaction tx = createBuyTransaction("BTC", 100.0, 5000.0);
        when(tx.getTransactionId()).thenReturn(id);
        when(tx.getFromPortfolio()).thenReturn(from);
        when(tx.getToPortfolio()).thenReturn(to);
        return tx;
    }

    private SellTransaction createSellTransaction(String asset, double quantity, double totalValue) {
        SellTransaction tx = mock(SellTransaction.class);
        when(tx.getTransactionId()).thenReturn("sell-" + asset);
        when(tx.getDate()).thenReturn(LocalDateTime.now());
        when(tx.getAssetSymbol()).thenReturn(asset);
        when(tx.getQuantity()).thenReturn(quantity);
        when(tx.getTotalValue()).thenReturn(totalValue);
        when(tx.getTransactionType()).thenReturn("SELL");
        return tx;
    }

    private SellTransaction createSellTransactionWithPortfolio(String id, String from, String to) {
        SellTransaction tx = createSellTransaction("ETH", 50.0, 3000.0);
        when(tx.getTransactionId()).thenReturn(id);
        when(tx.getFromPortfolio()).thenReturn(from);
        when(tx.getToPortfolio()).thenReturn(to);
        return tx;
    }

    private ConvertTransaction createConvertTransaction(String from, String to, double fromAmt, double toAmt) {
        ConvertTransaction tx = mock(ConvertTransaction.class);
        when(tx.getTransactionId()).thenReturn("convert-" + from + "-" + to);
        when(tx.getDate()).thenReturn(LocalDateTime.now());
        when(tx.getFromCurrency()).thenReturn(from);
        when(tx.getToCurrency()).thenReturn(to);
        when(tx.getFromAmount()).thenReturn(fromAmt);
        when(tx.getToAmount()).thenReturn(toAmt);
        when(tx.getTransactionType()).thenReturn("CONVERT");
        return tx;
    }

    private TransferTransaction createTransferTransaction(String asset, double quantity) {
        TransferTransaction tx = mock(TransferTransaction.class);
        when(tx.getTransactionId()).thenReturn("transfer-" + asset);
        when(tx.getDate()).thenReturn(LocalDateTime.now());
        when(tx.getAssetSymbol()).thenReturn(asset);
        when(tx.getQuantity()).thenReturn(quantity);
        when(tx.getTransactionType()).thenReturn("TRANSFER");
        return tx;
    }

    private TransferTransaction createTransferTransactionWithPortfolios(String from, String to) {
        TransferTransaction tx = createTransferTransaction("BTC", 10.0);
        when(tx.getFromPortfolio()).thenReturn(from);
        when(tx.getToPortfolio()).thenReturn(to);
        return tx;
    }
}
