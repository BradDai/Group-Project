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
        TransferInputData inputData = new TransferInputData(
                "Paul", "Main Portfolio", "Savings", "Currency", "USD", 50.0);

        // Initial state: Main=100 USD, Savings=0 USD
        InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);
        transferDataAccess.addAccount("Paul", "Savings", "USD", 0.0);

        TransferOutputBoundary successPresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(TransferOutputData outputData) {
                assertTrue(outputData.isSuccess());
                assertEquals("Main Portfolio", outputData.getFromPortfolio());
                assertEquals("Savings", outputData.getToPortfolio());
                assertEquals(50.0, outputData.getAmount());
                assertNotNull(outputData.getTransactionId());
                assertEquals(50.0, transferDataAccess.getAssetBalance("Paul", "Main Portfolio", "USD"));
                assertEquals(50.0, transferDataAccess.getAssetBalance("Paul", "Savings", "USD"));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected: " + error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] availableCurrencies) {
                fail("presentBalances should not be called in execute");
            }
        };

        TransferInteractor interactor = new TransferInteractor(transferDataAccess, successPresenter);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failSourcePortfolioNotFoundTest() {
        TransferInputData inputData = new TransferInputData(
                "Paul", "Ghost Portfolio", "Savings", "Currency", "USD", 50.0);

        InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Savings", "USD", 0.0);

        TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Source portfolio does not exist: Ghost Portfolio", error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] availableCurrencies) {
            }
        };

        TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failDestinationPortfolioNotFoundTest() {
        TransferInputData inputData = new TransferInputData(
                "Paul", "Main Portfolio", "Ghost Portfolio", "Currency", "USD", 50.0);

        InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);

        TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Destination portfolio does not exist: Ghost Portfolio", error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] availableCurrencies) {
            }
        };

        TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failSamePortfolioTest() {
        TransferInputData inputData = new TransferInputData(
                "Paul", "Main Portfolio", "Main Portfolio", "Currency", "USD", 50.0);

        InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);

        TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Cannot transfer to the same portfolio", error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] availableCurrencies) {
            }
        };

        TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failAssetNotFoundTest() {
        // Trying to transfer BRL, but only has USD
        TransferInputData inputData = new TransferInputData(
                "Paul", "Main Portfolio", "Savings", "Currency", "BRL", 50.0);

        InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);
        transferDataAccess.addAccount("Paul", "Savings", "USD", 0.0);

        TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Source portfolio does not contain asset: BRL", error);
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] availableCurrencies) {
            }
        };

        TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failInsufficientFundsTest() {
        // Has 20, tries to send 50
        TransferInputData inputData = new TransferInputData(
                "Paul", "Main Portfolio", "Savings", "Currency", "USD", 50.0);

        InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 20.0);
        transferDataAccess.addAccount("Paul", "Savings", "USD", 0.0);

        TransferOutputBoundary failurePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(TransferOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertTrue(error.startsWith("Insufficient balance"));
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] availableCurrencies) {
            }
        };

        TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void checkBalancesTest() {
        InMemoryTransferDataAccess transferDataAccess = new InMemoryTransferDataAccess();
        transferDataAccess.addAccount("Paul", "Main Portfolio", "USD", 100.0);
        transferDataAccess.addAccount("Paul", "Main Portfolio", "EUR", 50.0); // Added EUR
        transferDataAccess.addAccount("Paul", "Savings", "USD", 25.0);

        TransferOutputBoundary balancePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(TransferOutputData outputData) {
                fail("Should not call success view");
            }

            @Override
            public void prepareFailView(String error) {
                fail("Should not call fail view");
            }

            @Override
            public void presentBalances(double fromBalance, double toBalance, String[] availableCurrencies) {
                assertEquals(100.0, fromBalance);
                assertEquals(25.0, toBalance);

                // Check currencies
                List<String> currencies = Arrays.asList(availableCurrencies);
                assertTrue(currencies.contains("USD"));
                assertTrue(currencies.contains("EUR"));
                assertEquals(2, currencies.size());
            }
        };

        TransferInteractor interactor = new TransferInteractor(transferDataAccess, balancePresenter);

        // Act
        interactor.checkBalances("Paul", "Main Portfolio", "Savings", "USD");
    }

    /**
     * InMemory Implementation of TransferDataAccessInterface for testing.
     */
    private static class InMemoryTransferDataAccess implements TransferDataAccessInterface {
        // Map<Username, Map<PortfolioName, Map<AssetSymbol, Amount>>>
        private final Map<String, Map<String, Map<String, Double>>> accounts = new HashMap<>();

        // Helper to populate data
        public void addAccount(String username, String portfolio, String asset, Double amount) {
            accounts.computeIfAbsent(username, k -> new HashMap<>())
                    .computeIfAbsent(portfolio, k -> new HashMap<>())
                    .put(asset, amount);
        }

        @Override
        public boolean portfolioExists(String username, String portfolioId) {
            return accounts.containsKey(username) && accounts.get(username).containsKey(portfolioId);
        }

        @Override
        public boolean hasAsset(String username, String portfolioId, String assetSymbol) {
            if (!portfolioExists(username, portfolioId)) return false;
            return accounts.get(username).get(portfolioId).containsKey(assetSymbol);
        }

        @Override
        public double getAssetBalance(String username, String portfolioId, String assetSymbol) {
            if (!hasAsset(username, portfolioId, assetSymbol)) return 0.0;
            return accounts.get(username).get(portfolioId).get(assetSymbol);
        }

        @Override
        public void transferAsset(String username, String fromPortfolio, String toPortfolio, String assetSymbol, double amount) {
            double fromBal = getAssetBalance(username, fromPortfolio, assetSymbol);
            double toBal = getAssetBalance(username, toPortfolio, assetSymbol);

            accounts.get(username).get(fromPortfolio).put(assetSymbol, fromBal - amount);
            accounts.get(username).get(toPortfolio).putIfAbsent(assetSymbol, 0.0);
            double currentTo = accounts.get(username).get(toPortfolio).get(assetSymbol);
            accounts.get(username).get(toPortfolio).put(assetSymbol, currentTo + amount);
        }

        @Override
        public void saveTransaction(Transaction transaction) {
            // No-op for test
        }

        @Override
        public String[] getAvailablePortfolios(String username) {
            if (!accounts.containsKey(username)) return new String[0];
            return accounts.get(username).keySet().toArray(new String[0]);
        }

        @Override
        public String[] getAvailableStocks(String username, String portfolioId) {
            return new String[0];
        }

        @Override
        public String[] getAvailableCurrencies(String username, String portfolioId) {
            if (!portfolioExists(username, portfolioId)) return new String[0];
            return accounts.get(username).get(portfolioId).keySet().toArray(new String[0]);
        }

        @Override
        public double getStockPrice(String symbol) {
            return 100.0;
        }

        @Override
        public List<SubAccount> getSubAccountsOf(String username) {
            List<SubAccount> result = new ArrayList<>();
            if (accounts.containsKey(username)) {
                for (String portName : accounts.get(username).keySet()) {
                    // Create SubAccount for output data verification
                    // Assuming USD is the main balance for simplicity
                    Double usdBal = accounts.get(username).get(portName).getOrDefault("USD", 0.0);
                    result.add(new SubAccount(portName, BigDecimal.valueOf(usdBal), false));
                }
            }
            return result;
        }
    }
}