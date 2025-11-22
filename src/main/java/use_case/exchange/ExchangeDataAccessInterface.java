package use_case.exchange;

import java.util.Map;

public interface ExchangeDataAccessInterface {

    /**
     * Returns a map of currency -> amount for the given user's subaccount.
     */
    Map<String, Double> getCurrencies(String username, String accountName);

    /**
     * Persists the updated currency map for the given user's subaccount.
     */
    void saveCurrencies(String username, String accountName, Map<String, Double> currencies);
}
