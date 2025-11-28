package dataaccess;

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
import usecase.SubAccount.SubAccountDataAccessInterface;

public class FileSubAccountDataAccess implements SubAccountDataAccessInterface {

    private static final int PARTS_EXPECTED = 4;
    private static final int INDEX_USERNAME = 0;
    private static final int INDEX_SUB_NAME = 1;
    private static final int INDEX_BALANCE = 2;
    private static final int INDEX_UNDELETABLE = 3;

    private final Path filePath;
    private final Map<String, List<SubAccount>> data = new HashMap<>();

    public FileSubAccountDataAccess(final String filename) {
        this.filePath = Paths.get(filename);
        loadFromFile();
    }

    private void loadFromFile() {
        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    final String[] parts = line.split(",", -1);
                    if (parts.length != PARTS_EXPECTED) {
                        continue;
                    }

                    final String username = parts[INDEX_USERNAME].trim();
                    final String subName = parts[INDEX_SUB_NAME].trim();
                    final String balStr = parts[INDEX_BALANCE].trim();
                    final String undeletableStr = parts[INDEX_UNDELETABLE].trim();

                    if (username.isEmpty() || subName.isEmpty()) {
                        continue;
                    }

                    BigDecimal balance;
                    try {
                        balance = new BigDecimal(balStr);
                    }
                    catch (final NumberFormatException evt) {
                        balance = BigDecimal.ZERO;
                    }

                    final boolean undeletable = Boolean.parseBoolean(undeletableStr);

                    final SubAccount subAccount =
                            new SubAccount(subName, balance, undeletable);

                    final List<SubAccount> list = data.computeIfAbsent(
                            username,
                            userKey -> new ArrayList<>());

                    list.remove(subAccount);
                    list.add(subAccount);
                }
            }
            catch (final IOException evt) {
                throw new UncheckedIOException(evt);
            }
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
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
        catch (final IOException evt) {
            throw new UncheckedIOException(evt);
        }
    }

    @Override
    public boolean exists(final String username, final String subName) {
        return data.getOrDefault(username, List.of())
            .stream()
            .anyMatch(sub -> sub.getName().equalsIgnoreCase(subName));
    }

    @Override
    public void save(final String username, final SubAccount subAccount) {
        final List<SubAccount> list = data.computeIfAbsent(username,
            uuu -> new ArrayList<>());
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

        if (list != null) {
            list.removeIf(sub -> sub.getName().equalsIgnoreCase(subName));
            saveToFile();
        }
    }
}
