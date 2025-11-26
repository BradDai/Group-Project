package usecase.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dataaccess.TransactionDataAccessInterface;
import entity.SubAccount;
import entity.transaction.Transaction;

class TransferInteractorTest {

    @Test
    void successTransferTest() {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Savings", "Currency", "USD", 50.0);

        final Map<String, Map<String, Map<String, Double>>> data = new HashMap<>();
        addAccount(data, "Main Portfolio", "USD", 100.0);
        addAccount(data, "Savings", "USD", 0.0);

        final TransferDataAccessInterface transferDataAccess = createDataAccess(data);

        // Capture saved transactions
        final List<Transaction> savedTransactions = new ArrayList<>();
        final TransactionDataAccessInterface transactionRepo = createTransactionRepo(savedTransactions);

        class SuccessPresenter implements TransferOutputBoundary {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
                assertTrue(outputData.success());
                assertEquals("Main Portfolio", outputData.fromPortfolio());
                assertEquals("Savings", outputData.toPortfolio());
                assertEquals("USD", outputData.assetSymbol());
                assertEquals(50.0, outputData.amount());
                assertNotNull(outputData.transactionId());
                assertNotNull(outputData.updatedAccounts());

                // Verify DAO state change
                assertEquals(50.0, transferDataAccess.getAssetBalance("Paul", "Main Portfolio", "USD"));
                assertEquals(50.0, transferDataAccess.getAssetBalance("Paul", "Savings", "USD"));

                // Verify Transaction Saved
                assertEquals(1, savedTransactions.size());
                assertEquals(outputData.transactionId(), savedTransactions.get(0).getTransactionId());
            }

            @Override
            public void prepareFailView(final String error) {
                fail("Use case failure is unexpected: " + error);
            }

            @Override
            public void presentBalances(final double fromBalance, final double toBalance, final String[] currencyList,
                                        final String[] stockList) {
            }
        }

        final TransferOutputBoundary successPresenter = new SuccessPresenter();

        final TransferInteractor interactor =
            new TransferInteractor(transferDataAccess, successPresenter, transactionRepo);
        interactor.execute(inputData);
    }

    @Test
    void failSourcePortfolioNotFoundTest() {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Ghost Portfolio", "Savings", "Currency", "USD", 50.0);

        final Map<String, Map<String, Map<String, Double>>> data = new HashMap<>();
        addAccount(data, "Savings", "USD", 0.0);
        final TransferDataAccessInterface transferDataAccess = createDataAccess(data);

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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] currencyList,
                                        final String[] stockList) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter, null);
        interactor.execute(inputData);
    }

    @Test
    void failDestinationPortfolioNotFoundTest() {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Ghost Portfolio", "Currency", "USD", 50.0);

        final Map<String, Map<String, Map<String, Double>>> data = new HashMap<>();
        addAccount(data, "Main Portfolio", "USD", 100.0);
        final TransferDataAccessInterface transferDataAccess = createDataAccess(data);

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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] currencyList,
                                        final String[] stockList) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter, null);
        interactor.execute(inputData);
    }

    @Test
    void failSamePortfolioTest() {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Main Portfolio", "Currency", "USD", 50.0);

        final Map<String, Map<String, Map<String, Double>>> data = new HashMap<>();
        addAccount(data, "Main Portfolio", "USD", 100.0);
        final TransferDataAccessInterface transferDataAccess = createDataAccess(data);

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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] currencyList,
                                        final String[] stockList) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter, null);
        interactor.execute(inputData);
    }

    @Test
    void failAssetNotFoundTest() {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Savings", "Currency", "BRL", 50.0);

        final Map<String, Map<String, Map<String, Double>>> data = new HashMap<>();
        addAccount(data, "Main Portfolio", "USD", 100.0);
        addAccount(data, "Savings", "USD", 0.0);
        final TransferDataAccessInterface transferDataAccess = createDataAccess(data);

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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] currencyList,
                                        final String[] stockList) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter, null);
        interactor.execute(inputData);
    }

    @Test
    void failInsufficientFundsTest() {
        final TransferInputData inputData = new TransferInputData(
            "Paul", "Main Portfolio", "Savings", "Currency", "USD", 50.0);

        final Map<String, Map<String, Map<String, Double>>> data = new HashMap<>();
        addAccount(data, "Main Portfolio", "USD", 20.0);
        addAccount(data, "Savings", "USD", 0.0);
        final TransferDataAccessInterface transferDataAccess = createDataAccess(data);

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
            public void presentBalances(final double fromBalance, final double toBalance, final String[] currencyList,
                                        final String[] stockList) {
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, failurePresenter, null);
        interactor.execute(inputData);
    }

    @Test
    void checkBalancesTest() {
        final Map<String, Map<String, Map<String, Double>>> data = new HashMap<>();
        addAccount(data, "Main Portfolio", "USD", 100.0);
        addAccount(data, "Main Portfolio", "EUR", 50.0);
        addAccount(data, "Savings", "USD", 25.0);
        final TransferDataAccessInterface transferDataAccess = createDataAccess(data);

        class BalanceCheckingPresenter implements TransferOutputBoundary {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
                fail("Should not call success view");
            }

            @Override
            public void prepareFailView(final String error) {
                fail("Should not call fail view");
            }

            @Override
            public void presentBalances(final double fromBalance, final double toBalance, final String[] currencyList,
                                        final String[] stockList) {
                assertEquals(100.0, fromBalance);
                assertEquals(25.0, toBalance);
                final List<String> currencies = Arrays.asList(currencyList);
                assertTrue(currencies.contains("USD"));
                assertTrue(currencies.contains("EUR"));
                assertEquals(2, currencies.size());
            }
        }

        final TransferOutputBoundary balancePresenter = new BalanceCheckingPresenter();

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, balancePresenter, null);
        interactor.checkBalances("Paul", "Main Portfolio", "Savings", "USD");
    }

    @Test
    void checkBalancesMissingSourceTest() {
        final Map<String, Map<String, Map<String, Double>>> data = new HashMap<>();
        addAccount(data, "Savings", "USD", 25.0);
        final TransferDataAccessInterface transferDataAccess = createDataAccess(data);

        final TransferOutputBoundary balancePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
            }

            @Override
            public void prepareFailView(final String error) {
            }

            @Override
            public void presentBalances(final double fromBalance, final double toBalance,
                                        final String[] currencyList, final String[] stockList) {
                assertEquals(0.0, fromBalance, 0.001);
                assertEquals(25.0, toBalance, 0.001);
                assertEquals(1, currencyList.length);
                assertEquals("USD", currencyList[0]);
                assertEquals(0, stockList.length);
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, balancePresenter, null);
        interactor.checkBalances("Paul", "Missing Portfolio", "Savings", "USD");
    }

    @Test
    void checkBalancesMissingDestinationTest() {
        final Map<String, Map<String, Map<String, Double>>> data = new HashMap<>();
        addAccount(data, "Main Portfolio", "USD", 100.0);
        final TransferDataAccessInterface transferDataAccess = createDataAccess(data);

        final TransferOutputBoundary balancePresenter = new TransferOutputBoundary() {
            @Override
            public void prepareSuccessView(final TransferOutputData outputData) {
            }

            @Override
            public void prepareFailView(final String error) {

            }

            @Override
            public void presentBalances(final double fromBalance, final double toBalance,
                                        final String[] currencyList, final String[] stockList) {
                assertEquals(100.0, fromBalance, 0.001);
                assertEquals(0.0, toBalance, 0.001);
            }
        };

        final TransferInteractor interactor = new TransferInteractor(transferDataAccess, balancePresenter, null);
        interactor.checkBalances("Paul", "Main Portfolio", "Missing Portfolio", "USD");
    }

    private void addAccount(final Map<String, Map<String, Map<String, Double>>> data,
                            final String portfolio, final String asset, final Double amount) {
        data.computeIfAbsent("Paul", string -> new HashMap<>())
            .computeIfAbsent(portfolio, string -> new HashMap<>())
            .put(asset, amount);
    }

    /**
     * Creates an instance of the static inner class StubTransferDataAccess.
     */
    private TransferDataAccessInterface createDataAccess(final Map<String, Map<String, Map<String, Double>>> accounts) {
        return new StubTransferDataAccess(accounts);
    }

    /**
     * Creates an anonymous implementation of TransactionDataAccessInterface.
     */
    private TransactionDataAccessInterface createTransactionRepo(final List<Transaction> storage) {
        return new TransactionDataAccessInterface() {
            @Override
            public void save(final String userId, final Transaction transaction) {
                storage.add(transaction);
            }

            @Override
            public List<Transaction> getByPortfolio(final String userId, final String portfolioId) {
                return new ArrayList<>();
            }

            @Override
            public List<Transaction> getByFilters(final String userId, final String portfolioId,
                                                  final String assetSymbol, final LocalDate startDate,
                                                  final LocalDate endDate) {
                return new ArrayList<>();
            }
        };
    }

    /**
     * Stub implementation of TransferDataAccessInterface to avoid Checkstyle errors.
     */
    private static final class StubTransferDataAccess implements TransferDataAccessInterface {
        private final Map<String, Map<String, Map<String, Double>>> accounts;

        StubTransferDataAccess(final Map<String, Map<String, Map<String, Double>>> accounts) {
            this.accounts = accounts;
        }

        @Override
        public boolean portfolioExists(final String username, final String portfolioId) {
            return accounts.containsKey(username) && accounts.get(username).containsKey(portfolioId);
        }

        @Override
        public boolean hasAsset(final String username, final String portfolioId, final String assetSymbol) {
            return portfolioExists(username, portfolioId)
                && accounts.get(username).get(portfolioId).containsKey(assetSymbol);
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
        public void transferAsset(final String username, final String fromPortfolio, final String toPortfolio,
                                  final String assetSymbol, final double amount) {
            final double fromBal = getAssetBalance(username, fromPortfolio, assetSymbol);
            // Deduct
            accounts.get(username).get(fromPortfolio).put(assetSymbol, fromBal - amount);

            // Add
            accounts.get(username).get(toPortfolio).putIfAbsent(assetSymbol, 0.0);
            final double currentTo = accounts.get(username).get(toPortfolio).get(assetSymbol);
            accounts.get(username).get(toPortfolio).put(assetSymbol, currentTo + amount);
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
        public List<SubAccount> getSubAccountsOf(final String username) {
            final List<SubAccount> result = new ArrayList<>();
            if (accounts.containsKey(username)) {
                for (final String portName : accounts.get(username).keySet()) {
                    final Double usdBal = accounts.get(username).get(portName).getOrDefault("USD", 0.0);
                    result.add(new SubAccount(portName, BigDecimal.valueOf(usdBal), false));
                }
            }
            return result;
        }
    }
}
