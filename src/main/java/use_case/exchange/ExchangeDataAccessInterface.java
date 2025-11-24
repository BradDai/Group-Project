package use_case.exchange;

import entity.SubAccount;

import java.util.List;
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
    List<SubAccount> getSubAccountsOf(String username);
    Map<String, Double> getRates(String currency);
}
