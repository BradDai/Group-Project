package data_access;
import entity.SubAccount;
import use_case.SubAccount.SubAccountDataAccessInterface;
import java.util.*;
public class InMemorySubAccountDataAccess implements SubAccountDataAccessInterface {
    private final Map<String, Map<String, SubAccount>> data = new HashMap<>();
    @Override
    public void save(String username, SubAccount subAccount) {
        data.computeIfAbsent(username, u -> new LinkedHashMap<>())
                .put(subAccount.getName().toLowerCase(), subAccount);
    }

    @Override
    public List<SubAccount> findByUser(String username) {
        Map<String, SubAccount> map = data.get(username);
        if (map == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public boolean exists(String username, String subAccountName) {
        Map<String, SubAccount> map = data.get(username);
        return map != null && map.containsKey(subAccountName.toLowerCase());
    }
    @Override
    public int countByUser(String username) {
        Map<String, SubAccount> map = data.get(username);
        return map == null ? 0 : map.size();
    }
}
