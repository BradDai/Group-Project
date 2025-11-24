package data_access;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.SubAccount;
import use_case.SubAccount.SubAccountDataAccessInterface;

public class FileSubAccountDataAccess implements SubAccountDataAccessInterface {

    private final Path filePath;
    private final Map<String, List<SubAccount>> data = new HashMap<>();

    public FileSubAccountDataAccess(final String filename) {
        this.filePath = Paths.get(filename);
        loadFromFile();
    }

    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            return;
        }
        try (final BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                final String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    continue;
                }
                final String username = parts[0].trim();
                final String subName = parts[1].trim();
                final String balStr = parts[2].trim();
                final String undeletableStr = parts[3].trim();

                if (username.isEmpty() || subName.isEmpty()) {
                    continue;
                }

                BigDecimal balance;
                try {
                    balance = new BigDecimal(balStr);
                }
                catch (final NumberFormatException e) {
                    balance = BigDecimal.ZERO;
                }

                final boolean undeletable = Boolean.parseBoolean(undeletableStr);

                final SubAccount sa = new SubAccount(subName, balance, undeletable);

                final List<SubAccount> list = data.computeIfAbsent(username,
                    u -> new ArrayList<>());
                list.remove(sa);
                list.add(sa);
            }
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void saveToFile() {
        try (final BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (final Map.Entry<String, List<SubAccount>> entry : data.entrySet()) {
                final String username = entry.getKey();
                for (final SubAccount sa : entry.getValue()) {
                    final String line = String.join(",",
                        username,
                        sa.getName(),
                        sa.getBalanceUSD().toString(),
                        Boolean.toString(sa.isUndeletable()));
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean exists(final String username, final String subName) {
        return data.getOrDefault(username, List.of())
            .stream()
            .anyMatch(sa -> sa.getName().equalsIgnoreCase(subName));
    }

    @Override
    public void save(final String username, final SubAccount subAccount) {
        final List<SubAccount> list = data.computeIfAbsent(username,
            u -> new ArrayList<>());
        list.remove(subAccount);
        list.add(subAccount);
        saveToFile();
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
        list.removeIf(sa -> sa.getName().equalsIgnoreCase(subName));
        saveToFile();
    }
}
