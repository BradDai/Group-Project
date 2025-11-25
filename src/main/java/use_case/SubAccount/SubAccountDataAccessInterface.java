package use_case.SubAccount;

import java.util.List;

import entity.SubAccount;

public interface SubAccountDataAccessInterface {

    boolean exists(String username, String subName);

    void save(String username, SubAccount subAccount);

    void delete(String username, String subName);

    List<SubAccount> getSubAccountsOf(String username);

    int countByUser(String username);
}
