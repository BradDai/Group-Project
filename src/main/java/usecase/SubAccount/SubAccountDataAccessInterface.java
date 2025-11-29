package usecase.SubAccount;

import java.util.List;

import entity.SubAccount;

public interface SubAccountDataAccessInterface {

    /**
     * I.
     * @param username .
     * @param subName .
     * @return .
     */
    boolean exists(String username, String subName);

    /**
     * I.
     * @param username .
     * @param subAccount .
     */
    void save(String username, SubAccount subAccount);

    /**
     * I.
     * @param username .
     * @param subName .
     */
    void delete(String username, String subName);

    /**
     * I.
     * @param username .
     * @return .
     */
    List<SubAccount> getSubAccountsOf(String username);

    /**
     * Y.
     * @param username .
     * @return .
     */
    int countByUser(String username);
}
