package usecase.exchange;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.Test;

import dataaccess.TransactionDataAccessInterface;
import entity.SubAccount;
import entity.transaction.Transaction;
import entity.transaction.ConvertTransaction;

class ExchangeInteractorTest {

    // ------------------------------------------------------------
    // Helper: stub transaction repo
    // ------------------------------------------------------------
    private TransactionDataAccessInterface createTransactionRepo(List<Transaction> storage) {
        return new TransactionDataAccessInterface() {

            @Override
            public void save(String userId, Transaction transaction) {
                storage.add(transaction);
            }

            @Override
            public List<Transaction> getByPortfolio(String userId, String portfolioId) {
                return new ArrayList<>();
            }

            @Override
            public List<Transaction> getByFilters(String userId, String portfolioId, String assetSymbol,
                                                  LocalDate startDate, LocalDate endDate) {
                return new ArrayList<>();
            }
        };
    }

    // ------------------------------------------------------------
    // Helper: Stub Data Access
    // ------------------------------------------------------------
    private static final class StubExchangeDAO implements ExchangeDataAccessInterface {

        Map<String, Map<String, Double>> currenciesByAccount; // accountName -> currencyMap
        Map<String, Map<String, Double>> rates; // baseCurrency -> (target -> rate)
        List<SubAccount> subAccounts;

        StubExchangeDAO(
                Map<String, Map<String, Double>> currenciesByAccount,
                Map<String, Map<String, Double>> rates,
                List<SubAccount> subAccounts
        ) {
            this.currenciesByAccount = currenciesByAccount;
            this.rates = rates;
            this.subAccounts = subAccounts;
        }

        @Override
        public Map<String, Double> getCurrencies(String username, String accountName) {
            return currenciesByAccount.get(accountName);
        }

        @Override
        public void saveCurrencies(String username, String accountName, Map<String, Double> currencies) {
            currenciesByAccount.put(accountName, currencies);
        }

        @Override
        public List<SubAccount> getSubAccountsOf(String username) {
            return subAccounts;
        }

        @Override
        public Map<String, Double> getRates(String currency) {
            return rates.get(currency);
        }
    }

    // ------------------------------------------------------------
    // Test: fetchExchangeRate success
    // ------------------------------------------------------------
    @Test
    void fetchExchangeRateSuccess() {
        Map<String, Map<String, Double>> rates = new HashMap<>();
        rates.put("USD", Map.of("EUR", 0.9));

        ExchangeDataAccessInterface dao = new StubExchangeDAO(
                new HashMap<>(),
                rates,
                List.of()
        );

        class Presenter implements ExchangeOutputBoundary {

            @Override
            public void presentSuccess(ExchangeOutputData outputData) {
                assertEquals("USD", outputData.from());
                assertEquals("EUR", outputData.to());
                assertEquals(0.9, outputData.rate());
            }

            @Override
            public void presentFailure(String errorMessage) {
                fail("Should not fail");
            }

            @Override
            public void presentConversionFailure(String errorMessage) {
                fail("Should not call conversion failure");
            }

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) {
                fail("Should not call conversion success");
            }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(),
                dao,
                null
        );

        interactor.fetchExchangeRate(new ExchangeInputData("USD", "EUR"));
    }

    // ------------------------------------------------------------
    // Test: fetchExchangeRate invalid target
    // ------------------------------------------------------------
    @Test
    void fetchExchangeRateInvalidTarget() {
        Map<String, Map<String, Double>> rates = new HashMap<>();
        rates.put("USD", Map.of("GBP", 0.8));

        ExchangeDataAccessInterface dao = new StubExchangeDAO(
                new HashMap<>(), rates, List.of()
        );

        class Presenter implements ExchangeOutputBoundary {

            @Override
            public void presentSuccess(ExchangeOutputData outputData) {
                fail("Should not succeed");
            }

            @Override
            public void presentFailure(String errorMessage) {
                assertEquals("Invalid target currency.", errorMessage);
            }

            @Override
            public void presentConversionFailure(String errorMessage) { }

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) { }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(),
                dao,
                null
        );

        interactor.fetchExchangeRate(new ExchangeInputData("USD", "EUR"));
    }

    // ------------------------------------------------------------
    // Test: fetchExchangeRate exception
    // ------------------------------------------------------------
    @Test
    void fetchExchangeRateException() {

        ExchangeDataAccessInterface dao = new ExchangeDataAccessInterface() {

            @Override
            public Map<String, Double> getCurrencies(String username, String accountName) { return null; }

            @Override
            public void saveCurrencies(String username, String accountName, Map<String, Double> currencies) { }

            @Override
            public List<SubAccount> getSubAccountsOf(String username) { return null; }

            @Override
            public Map<String, Double> getRates(String currency) {
                throw new RuntimeException("DAO ERROR");
            }
        };

        class Presenter implements ExchangeOutputBoundary {
            @Override
            public void presentSuccess(ExchangeOutputData outputData) { fail("Should not succeed"); }

            @Override
            public void presentFailure(String errorMessage) {
                assertTrue(errorMessage.contains("DAO ERROR"));
            }

            @Override
            public void presentConversionFailure(String errorMessage) { }

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) { }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(),
                dao,
                null
        );

        interactor.fetchExchangeRate(new ExchangeInputData("USD", "EUR"));
    }

    // ------------------------------------------------------------
    // Test: convert success
    // ------------------------------------------------------------
    @Test
    void convertSuccessTest() {
        Map<String, Map<String, Double>> currencyMap = new HashMap<>();
        currencyMap.put("Main", new HashMap<>(Map.of("USD", 100.0)));

        Map<String, Map<String, Double>> rates = new HashMap<>();
        rates.put("USD", Map.of("EUR", 0.5));

        List<SubAccount> subs = List.of(
                new SubAccount("Main", java.math.BigDecimal.valueOf(50), false)
        );

        List<Transaction> txs = new ArrayList<>();

        StubExchangeDAO dao = new StubExchangeDAO(currencyMap, rates, subs);
        TransactionDataAccessInterface txRepo = createTransactionRepo(txs);

        class Presenter implements ExchangeOutputBoundary {

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) {
                assertEquals("Main", outputData.getAccountName());
                assertEquals("USD", outputData.getFrom());
                assertEquals("EUR", outputData.getTo());

                assertEquals(20.0, outputData.getAmountGiven());
                assertEquals(10.0, outputData.getAmountReceived());
                assertEquals(0.5, outputData.getRateUsed());

                // Updated balances
                assertEquals(80.0, outputData.getFromBalanceAfter());
                assertEquals(10.0, outputData.getToBalanceAfter());

                // DAO mutated state
                assertEquals(80.0, dao.currenciesByAccount.get("Main").get("USD"));
                assertEquals(10.0, dao.currenciesByAccount.get("Main").get("EUR"));

                // Transaction saved
                assertEquals(1, txs.size());
                assertTrue(txs.get(0) instanceof ConvertTransaction);

                // Subaccounts should match DAO
                assertNotNull(outputData.getUpdatedSubAccounts());
            }

            @Override
            public void presentConversionFailure(String errorMessage) {
                fail("Should not fail");
            }

            @Override
            public void presentSuccess(ExchangeOutputData outputData) { }

            @Override
            public void presentFailure(String errorMessage) { }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(),
                dao,
                txRepo
        );

        interactor.convert(new ExchangeConversionInputData("Paul", "Main", "USD", "EUR", 20.0));
    }

    // ------------------------------------------------------------
    // Test: amount <= 0
    // ------------------------------------------------------------
    @Test
    void convertInvalidAmount() {
        ExchangeDataAccessInterface dao = new StubExchangeDAO(
                new HashMap<>(),
                new HashMap<>(),
                List.of()
        );

        class Presenter implements ExchangeOutputBoundary {

            @Override
            public void presentConversionFailure(String errorMessage) {
                assertEquals("Amount must be positive.", errorMessage);
            }

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) {
                fail("Should not succeed");
            }

            @Override
            public void presentSuccess(ExchangeOutputData outputData) { }

            @Override
            public void presentFailure(String errorMessage) { }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(), dao, null
        );

        interactor.convert(new ExchangeConversionInputData("Paul", "Main", "USD", "EUR", 0));
    }

    // ------------------------------------------------------------
    // Test: same currency
    // ------------------------------------------------------------
    @Test
    void convertSameCurrency() {
        ExchangeDataAccessInterface dao = new StubExchangeDAO(
                new HashMap<>(), new HashMap<>(), List.of()
        );

        class Presenter implements ExchangeOutputBoundary {

            @Override
            public void presentConversionFailure(String errorMessage) {
                assertEquals("Source and target currency must be different.", errorMessage);
            }

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) { fail("Should not succeed"); }

            @Override
            public void presentSuccess(ExchangeOutputData outputData) { }

            @Override
            public void presentFailure(String errorMessage) { }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(), dao, null
        );

        interactor.convert(new ExchangeConversionInputData("Paul", "Main", "USD", "USD", 10));
    }

    // ------------------------------------------------------------
    // Test: missing source currency
    // ------------------------------------------------------------
    @Test
    void convertMissingSourceCurrency() {
        Map<String, Map<String, Double>> currencies = new HashMap<>();
        currencies.put("Main", new HashMap<>(Map.of("EUR", 10.0)));

        ExchangeDataAccessInterface dao = new StubExchangeDAO(
                currencies, new HashMap<>(), List.of()
        );

        class Presenter implements ExchangeOutputBoundary {

            @Override
            public void presentConversionFailure(String errorMessage) {
                assertEquals("Account does not own currency: USD", errorMessage);
            }

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) { fail("Should not succeed"); }

            @Override
            public void presentSuccess(ExchangeOutputData outputData) { }

            @Override
            public void presentFailure(String errorMessage) { }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(), dao, null
        );

        interactor.convert(new ExchangeConversionInputData("Paul", "Main", "USD", "EUR", 5));
    }

    // ------------------------------------------------------------
    // Test: insufficient source balance
    // ------------------------------------------------------------
    @Test
    void convertInsufficientBalance() {
        Map<String, Map<String, Double>> currencies = new HashMap<>();
        currencies.put("Main", new HashMap<>(Map.of("USD", 5.0)));

        ExchangeDataAccessInterface dao = new StubExchangeDAO(
                currencies, new HashMap<>(), List.of()
        );

        class Presenter implements ExchangeOutputBoundary {

            @Override
            public void presentConversionFailure(String errorMessage) {
                assertEquals("Insufficient USD balance.", errorMessage);
            }

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) { fail("Should not succeed"); }

            @Override
            public void presentSuccess(ExchangeOutputData outputData) { }

            @Override
            public void presentFailure(String errorMessage) { }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(), dao, null
        );

        interactor.convert(new ExchangeConversionInputData("Paul", "Main", "USD", "EUR", 10));
    }

    // ------------------------------------------------------------
    // Test: invalid target currency
    // ------------------------------------------------------------
    @Test
    void convertInvalidTargetCurrency() {
        Map<String, Map<String, Double>> currencies = new HashMap<>();
        currencies.put("Main", new HashMap<>(Map.of("USD", 100.0)));

        Map<String, Map<String, Double>> rates = new HashMap<>();
        rates.put("USD", Map.of("GBP", 0.8)); // no EUR rate

        ExchangeDataAccessInterface dao = new StubExchangeDAO(
                currencies, rates, List.of()
        );

        class Presenter implements ExchangeOutputBoundary {

            @Override
            public void presentConversionFailure(String errorMessage) {
                assertEquals("Invalid target currency.", errorMessage);
            }

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) { fail("Should not succeed"); }

            @Override
            public void presentSuccess(ExchangeOutputData outputData) { }

            @Override
            public void presentFailure(String errorMessage) { }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(), dao, null
        );

        interactor.convert(new ExchangeConversionInputData("Paul", "Main", "USD", "EUR", 10));
    }

    // ------------------------------------------------------------
    // Test: DAO exception while converting
    // ------------------------------------------------------------
    @Test
    void convertExceptionThrown() {

        ExchangeDataAccessInterface dao = new ExchangeDataAccessInterface() {

            @Override
            public Map<String, Double> getCurrencies(String username, String accountName) {
                throw new RuntimeException("CURRENCY FAIL");
            }

            @Override
            public void saveCurrencies(String username, String accountName, Map<String, Double> currencies) { }

            @Override
            public List<SubAccount> getSubAccountsOf(String username) { return List.of(); }

            @Override
            public Map<String, Double> getRates(String currency) { return Map.of(); }
        };

        class Presenter implements ExchangeOutputBoundary {

            @Override
            public void presentConversionFailure(String errorMessage) {
                assertTrue(errorMessage.contains("CURRENCY FAIL"));
            }

            @Override
            public void presentConversionSuccess(ExchangeConversionOutputData outputData) {
                fail("Should not succeed");
            }

            @Override
            public void presentSuccess(ExchangeOutputData outputData) { }

            @Override
            public void presentFailure(String errorMessage) { }
        }

        ExchangeInteractor interactor = new ExchangeInteractor(
                new Presenter(), dao, null
        );

        interactor.convert(new ExchangeConversionInputData("Paul", "Main", "USD", "EUR", 10));
    }
}
