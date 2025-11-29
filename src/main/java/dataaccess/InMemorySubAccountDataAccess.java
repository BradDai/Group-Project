package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.SubAccount;
import usecase.SubAccount.SubAccountDataAccessInterface;

public class InMemorySubAccountDataAccess implements SubAccountDataAccessInterface {

    private final Map<String, List<SubAccount>> data = new HashMap<>();

    @Override
    public boolean exists(final String username, final String subName) {
        return data.getOrDefault(username, List.of())
            .stream()
            .anyMatch(ssa -> ssa.getName().equalsIgnoreCase(subName));
    }

    @Override
    public void save(final String username, final SubAccount sub) {
        final List<SubAccount> list = data.computeIfAbsent(username, uuu -> new ArrayList<>());
        list.remove(sub);
        list.add(sub);
    }

    @Override
    public List<SubAccount> getSubAccountsOf(final String username) {
        return new ArrayList<>(data.getOrDefault(username, List.of()));
    }

    @Override
    public int countByUser(final String username) {
        return data.getOrDefault(username, List.of()).size();
    }

    @Override
    public void delete(final String username, final String subName) {
        final List<SubAccount> list = data.get(username);
        if (list == null) {
            return;
        }
        list.removeIf(ssa -> ssa.getName().equalsIgnoreCase(subName));
    }
}
