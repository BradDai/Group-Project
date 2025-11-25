package use_case.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import entity.SubAccount;
import entity.transaction.Transaction;

class TransferInteractorTest {

    @Test
    void successTransferTest() throws Exception {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Savings", "Currency", "USD", 50.0);

        // Initial state: Main=100 USD, Savings=0 USD
        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);
        transferDataAccess.addAccount("Paul", "Savings", "USD", 0.0);

        final TransferOutputBoundary successPresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
                assertTrue(outputData.isSuccess());
                assertEquals("Main Portfolio", outputData.getFromPortfolio());
                assertEquals("Savings", outputData.getToPortfolio());
                assertEquals("USD", outputData.getAssetSymbol());
                assertEquals(50.0, outputData.getAmount());
                assertNotNull(outputData.getTransactionId());
                assertNotNull(outputData.getUpdatedAccounts());
                assertEquals(50.0, transferDataAccess.getAssetBalance("Paul", "Main Portfolio", "USD"));
                assertEquals(50.0, transferDataAccess.getAssetBalance("Paul", "Savings", "USD"));
            }

            @Override
            public void prepareFailView(final String error) {
                fail("Use case failure is unexpected: " + error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] currencyList,
                                        String[] stockList) {
            }

            @Override
            public void presentBalances(final double fromBalance, final double toBalance,
                                        final String[] availableCurrencies) {
                fail("presentBalances should not be called in execute");
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    void failSourcePortfolioNotFoundTest() throws Exception {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Ghost Portfolio", "Savings", "Currency", "USD", 50.0);

        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Savings", "USD", 0.0);

        final TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(final String error) {
                assertEquals("Source portfolio does not exist: Ghost Portfolio", error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] currencyList,
                                        String[] stockList) {
            }

            @Override
            public void presentBalances(final double fromBalance, final double toBalance,
                                        final String[] availableCurrencies) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failDestinationPortfolioNotFoundTest() throws Exception {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Ghost Portfolio", "Currency", "USD", 50.0);

        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);

        final TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(final String error) {
                assertEquals("Destination portfolio does not exist: Ghost Portfolio", error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] currencyList,
                                        String[] stockList) {
            }

            @Override
            public void presentBalances(final double fromBalance,
                                        final double toBalance, final String[] availableCurrencies) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failSamePortfolioTest() throws Exception {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Main Portfolio", "Currency", "USD", 50.0);

        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);

        final TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(final String error) {
                assertEquals("Cannot transfer to the same portfolio", error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] currencyList,
                                        String[] stockList) {
            }

            @Override
            public void presentBalances(final double fromBalance,
                                        final double toBalance, final String[] availableCurrencies) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failAssetNotFoundTest() throws Exception {
        // Trying to transfer BRL, but only has USD
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Savings", "Currency", "BRL", 50.0);

        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);
        transferDataAccess.addAccount("Paul", "Savings", "USD", 0.0);

        final TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(final String error) {
                assertEquals("Source portfolio does not contain asset: BRL", error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] currencyList,
                                        String[] stockList) {
            }

            @Override
            public void presentBalances(final double fromBalance,
                                        final double toBalance, final String[] availableCurrencies) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failInsufficientFundsTest() throws Exception {
        // Has 20, tries to send 50
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Savings", "Currency", "USD", 50.0);

        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 20.0);
        transferDataAccess.addAccount("Paul", "Savings", "USD", 0.0);

        final TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(final String error) {
                assertTrue(error.startsWith("Insufficient balance"));
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] currencyList,
                                        String[] stockList) {
            }

            @Override
            public void presentBalances(final double fromBalance,
                                        final double toBalance, final String[] availableCurrencies) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void checkBalancesTest() {
        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);
        transferDataAccess.addAccount("Paul", "Main Portfolio", "EUR", 50.0);
        transferDataAccess.addAccount("Paul", "Savings", "USD", 25.0);

        final TransferOutputBoundary balancePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
                fail("Should not call success view");
            }

            @Override
            public void prepareFailView(final String error) {
                fail("Should not call fail view");
            }

            // Fixed: Moved assertions to the correct method signature (4 args) used by Interactor
            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] currencyList,
                                        String[] stockList) {
                assertEquals(100.0, fromBalance);
                assertEquals(25.0, toBalance);

                // Check currencies
                final List<String> currencies = Arrays.asList(currencyList);
                assertTrue(currencies.contains("USD"));
                assertTrue(currencies.contains("EUR"));
                assertEquals(2, currencies.size());
            }

            @Override
            public void presentBalances(final double fromBalance,
                                        final double toBalance, final String[] availableCurrencies) {
                fail("Should not call the 3-arg presentBalances");
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, balancePresenter);
        interactor.checkBalances("Paul", "Main Portfolio", "Savings", "USD");
    }

    @Test
    void checkBalancesMissingSourceTest() {
        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        // Only create destination
        transferDataAccess.addAccount("Paul", "Savings", "USD", 25.0);

        final TransferOutputBoundary balancePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
            }

            @Override
            public void prepareFailView(final String error) {
            }

            @Override
            public void presentBalances(final double fromBalance,
                                        final double toBalance, final String[] availableCurrencies) {
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance,
                                        String[] currencyList, String[] stockList) {
                // Assert defaults when source is missing
                assertEquals(0.0, fromBalance, 0.001);
                assertEquals(25.0, toBalance, 0.001);
                // Should return default ["USD"]
                assertEquals(1, currencyList.length);
                assertEquals("USD", currencyList[0]);
                assertEquals(0, stockList.length);
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, balancePresenter);
        interactor.checkBalances("Paul", "Missing Portfolio", "Savings", "USD");
    }

    @Test
    void checkBalancesMissingDestinationTest() {
        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        // Only create source
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);

        final TransferOutputBoundary balancePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
            }

            @Override
            public void prepareFailView(final String error) {

            }

            @Override
            public void presentBalances(final double fromBalance,
                                        final double toBalance, final String[] availableCurrencies) {
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance,
                                        String[] currencyList, String[] stockList) {
                // Assert defaults when dest is missing
                assertEquals(100.0, fromBalance, 0.001);
                assertEquals(0.0, toBalance, 0.001);
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, balancePresenter);
        interactor.checkBalances("Paul", "Main Portfolio", "Missing Portfolio", "USD");
    }

    /**
     * InMemory Implementation of TransferDataAccessInterface for testing.
     */
    private static final class InMemoryTransferDataAccess implements TransferDataAccessInterface {
        private final Map<String, Map<String, Map<String, Double>>> accounts = new HashMap<>();

        // Helper to populate data
        public void addAccount(final String username, final String portfolio, final String asset, final Double amount) {
            accounts.computeIfAbsent(username, string -> new HashMap<>())
                .computeIfAbsent(portfolio, string -> new HashMap<>())
                .put(asset, amount);
        }

        @Override
        public boolean portfolioExists(final String username, final String portfolioId) {
            return accounts.containsKey(username) && accounts.get(username).containsKey(portfolioId);
        }

        @Override
        public boolean hasAsset(final String username, final String portfolioId, final String assetSymbol) {
            boolean result = false;
            if (portfolioExists(username, portfolioId)) {
                result = accounts.get(username).get(portfolioId).containsKey(assetSymbol);
            }
            return result;
        }

        @Override
        public double getAssetBalance(final String username, final String portfolioId, final String assetSymbol) {
            double result = 0.0;
            if (hasAsset(username, portfolioId, assetSymbol)) {
                result = accounts.get(username).get(portfolioId).get(assetSymbol);
            }
            return result;
        }

        @Override
        public void transferAsset(final String username, final String fromPortfolio,
                                  final String toPortfolio, final String assetSymbol, final double amount) {
            final double fromBal = getAssetBalance(username, fromPortfolio, assetSymbol);

            accounts.get(username).get(fromPortfolio).put(assetSymbol, fromBal - amount);
            accounts.get(username).get(toPortfolio).putIfAbsent(assetSymbol, 0.0);
            final double currentTo = accounts.get(username).get(toPortfolio).get(assetSymbol);
            accounts.get(username).get(toPortfolio).put(assetSymbol, currentTo + amount);
        }

        @Override
        public void saveTransaction(final Transaction transaction) {
            // No-op for test
        }

        @Override
        public String[] getAvailablePortfolios(final String username) {
            final String[] result;
            if (!accounts.containsKey(username)) {
                result = new String[0];
            }
            else {
                result = accounts.get(username).keySet().toArray(new String[0]);
            }
            return result;
        }

        @Override
        public String[] getAvailableStocks(final String username, final String portfolioId) {
            return new String[0];
        }

        @Override
        public String[] getAvailableCurrencies(final String username, final String portfolioId) {
            final String[] result;
            if (!portfolioExists(username, portfolioId)) {
                result = new String[0];
            }
            else {
                result = accounts.get(username).get(portfolioId).keySet().toArray(new String[0]);
            }
            return result;
        }

        @Override
        public double getStockPrice(final String symbol) {
            return 100.0;
        }

        @Override
        public List<SubAccount> getSubAccountsOf(final String username) {
            final List<SubAccount> result = new ArrayList<>();
            if (accounts.containsKey(username)) {
                for (final String portName : accounts.get(username).keySet()) {
                    // Create SubAccount for output data verification
                    // Assuming USD is the main balance for simplicity
                    final Double usdBal = accounts.get(username).get(portName).getOrDefault("USD", 0.0);
                    result.add(new SubAccount(portName, BigDecimal.valueOf(usdBal), false));
                }
            }
            return result;
        }
    }
}
