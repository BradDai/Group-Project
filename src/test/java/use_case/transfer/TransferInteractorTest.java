package use_case.transfer;

import entity.SubAccount;
import entity.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TransferInteractorTest {

    @Test
    void successTransferTest() {
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
                assertEquals(50.0, outputData.getAmount());
                assertNotNull(outputData.getTransactionId());
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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] availableCurrencies) {
                fail("presentBalances should not be called in execute");
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, successPresenter);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failSourcePortfolioNotFoundTest() {
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
    void failDestinationPortfolioNotFoundTest() {
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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] availableCurrencies) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failSamePortfolioTest() {
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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] availableCurrencies) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failAssetNotFoundTest() {
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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] availableCurrencies) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failInsufficientFundsTest() {
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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] availableCurrencies) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void checkBalancesTest() {
        final InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);
        transferDataAccess.addAccount("Paul", "Main Portfolio", "EUR", 50.0); // Added EUR
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

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] currencyList,
                                        String[] stockList) {
            }

            @Override
            public void presentBalances(final double fromBalance, final double toBalance, final String[] availableCurrencies) {
                assertEquals(100.0, fromBalance);
                assertEquals(25.0, toBalance);

                // Check currencies
                final List<String> currencies = Arrays.asList(availableCurrencies);
                assertTrue(currencies.contains("USD"));
                assertTrue(currencies.contains("EUR"));
                assertEquals(2, currencies.size());
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, balancePresenter);

        // Act
        interactor.checkBalances("Paul", "Main Portfolio", "Savings", "USD");
    }

    /**
     * InMemory Implementation of TransferDataAccessInterface for testing.
     */
    private static class InMemoryTransferDataAccess implements TransferDataAccessInterface {
        private final Map<String, Map<String, Map<String, Double>>> accounts = new HashMap<>();

        // Helper to populate data
        public void addAccount(final String username, final String portfolio, final String asset, final Double amount) {
            accounts.computeIfAbsent(username, k -> new HashMap<>())
                    .computeIfAbsent(portfolio, k -> new HashMap<>())
                    .put(asset, amount);
        }

        @Override
        public boolean portfolioExists(final String username, final String portfolioId) {
            return accounts.containsKey(username) && accounts.get(username).containsKey(portfolioId);
        }

        @Override
        public boolean hasAsset(final String username, final String portfolioId, final String assetSymbol) {
            if (!portfolioExists(username, portfolioId)) return false;
            return accounts.get(username).get(portfolioId).containsKey(assetSymbol);
        }

        @Override
        public double getAssetBalance(final String username, final String portfolioId, final String assetSymbol) {
            if (!hasAsset(username, portfolioId, assetSymbol)) return 0.0;
            return accounts.get(username).get(portfolioId).get(assetSymbol);
        }

        @Override
        public void transferAsset(final String username, final String fromPortfolio, final String toPortfolio, final String assetSymbol, final double amount) {
            final double fromBal = getAssetBalance(username, fromPortfolio, assetSymbol);
            final double toBal = getAssetBalance(username, toPortfolio, assetSymbol);

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
            if (!accounts.containsKey(username)) return new String[0];
            return accounts.get(username).keySet().toArray(new String[0]);
        }

        @Override
        public String[] getAvailableStocks(final String username, final String portfolioId) {
            return new String[0];
        }

        @Override
        public String[] getAvailableCurrencies(final String username, final String portfolioId) {
            if (!portfolioExists(username, portfolioId)) return new String[0];
            return accounts.get(username).get(portfolioId).keySet().toArray(new String[0]);
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
