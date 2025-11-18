package data_access;

import entity.SubAccount;
import use_case.SubAccount.SubAccountDataAccessInterface;

import java.util.*;

public class InMemorySubAccountDataAccess implements SubAccountDataAccessInterface {

    private final Map<String, List<SubAccount>> data = new HashMap<>();
    @Override
    public boolean exists(String username, String subName) {
        return data.getOrDefault(username, List.of())
                .stream()
                .anyMatch(sa -> sa.getName().equalsIgnoreCase(subName));
    }
    @Override
    public void save(String username, SubAccount sub) {
        List<SubAccount> list = data.computeIfAbsent(username, u -> new ArrayList<>());
        list.remove(sub);
        list.add(sub);
    }
    @Override
    public List<SubAccount> getSubAccountsOf(String username) {
        return new ArrayList<>(data.getOrDefault(username, List.of()));
    }
    @Override
    public int countByUser(String username) {
        return data.getOrDefault(username, List.of()).size();
    }
}