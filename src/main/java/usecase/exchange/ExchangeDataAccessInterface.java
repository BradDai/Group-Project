package usecase.exchange;

import java.util.List;
import java.util.Map;

import entity.SubAccount;

public interface ExchangeDataAccessInterface {

    /**
     * Returns a map of currency -> amount for the given user's subaccount.
     * @param username .
     * @param accountName .
     */
    Map<String, Double> getCurrencies(String username, String accountName);

    /**
     * Persists the updated currency map for the given user's subaccount.
     * @param username .
     * @param accountName .
     * @param currencies .
     */
    void saveCurrencies(String username, String accountName, Map<String, Double> currencies);

    /**
     * I.
     * @param username .
     * @return .
     */
    List<SubAccount> getSubAccountsOf(String username);

    /**
     * I.
     * @param currency .
     * @return .
     */
    Map<String, Double> getRates(String currency);
}
