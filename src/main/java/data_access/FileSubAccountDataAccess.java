package data_access;

import entity.SubAccount;
import use_case.SubAccount.SubAccountDataAccessInterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileSubAccountDataAccess implements SubAccountDataAccessInterface {

    private final Path filePath;
    private final Map<String, List<SubAccount>> data = new HashMap<>();
    public FileSubAccountDataAccess(String filename) {
        this.filePath = Paths.get(filename);
        loadFromFile();
    }
    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    continue;
                }
                String username = parts[0].trim();
                String subName  = parts[1].trim();
                String balStr   = parts[2].trim();
                String undeletableStr = parts[3].trim();

                if (username.isEmpty() || subName.isEmpty()) {
                    continue;
                }

                BigDecimal balance;
                try {
                    balance = new BigDecimal(balStr);
                } catch (NumberFormatException e) {
                    balance = BigDecimal.ZERO;
                }

                boolean undeletable = Boolean.parseBoolean(undeletableStr);

                SubAccount sa = new SubAccount(subName, balance, undeletable);

                List<SubAccount> list = data.computeIfAbsent(username,
                        u -> new ArrayList<>());
                list.remove(sa);
                list.add(sa);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    private void saveToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Map.Entry<String, List<SubAccount>> entry : data.entrySet()) {
                String username = entry.getKey();
                for (SubAccount sa : entry.getValue()) {
                    String line = String.join(",",
                            username,
                            sa.getName(),
                            sa.getBalanceUSD().toString(),
                            Boolean.toString(sa.isUndeletable()));
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean exists(String username, String subName) {
        return data.getOrDefault(username, List.of())
                .stream()
                .anyMatch(sa -> sa.getName().equalsIgnoreCase(subName));
    }

    @Override
    public void save(String username, SubAccount subAccount) {
        List<SubAccount> list = data.computeIfAbsent(username,
                u -> new ArrayList<>());
        list.remove(subAccount);
        list.add(subAccount);
        saveToFile();
    }

    @Override
    public List<SubAccount> getSubAccountsOf(String username) {
        return new ArrayList<>(data.getOrDefault(username, List.of()));
    }

    @Override
    public int countByUser(String username) {
        return data.getOrDefault(username, List.of()).size();
    }

    @Override
    public void delete(String username, String subName) {
        List<SubAccount> list = data.get(username);
        if (list == null) {
            return;
        }
        list.removeIf(sa -> sa.getName().equalsIgnoreCase(subName));
        saveToFile();
    }
}