package use_case.SubAccount;
import entity.SubAccount;
import java.util.List;
public interface SubAccountDataAccessInterface {
    void save(String username, SubAccount subAccount);
    List<SubAccount> findByUser(String username);
    boolean exists(String username, String subAccountName);
    int countByUser(String username);
}
