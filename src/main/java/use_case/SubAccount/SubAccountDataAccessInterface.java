package use_case.SubAccount;

import entity.SubAccount;
import java.util.List;

public interface SubAccountDataAccessInterface {

    boolean exists(String username, String subName);

    void save(String username, SubAccount subAccount);

    List<SubAccount> getSubAccountsOf(String username);

    int countByUser(String username);
}