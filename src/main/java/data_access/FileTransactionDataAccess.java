package data_access;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entity.transaction.BuyTransaction;
import entity.transaction.SellTransaction;
import entity.transaction.Transaction;

/**
 * File-based implementation of TransactionDataAccessInterface.
 * Stores one CSV file per user: transactions_<userId>.csv
 */
public class FileTransactionDataAccess implements TransactionDataAccessInterface {

    private final Path transactionsDirectory;

    /**
     * @param directoryPath directory where transaction files are stored,
     *                      e.g. "data/transactions"
     */
    public FileTransactionDataAccess(final String directoryPath) {
        this.transactionsDirectory = Paths.get(directoryPath);
        try {
            if (!Files.exists(transactionsDirectory)) {
                Files.createDirectories(transactionsDirectory);
            }
        }
        catch (final IOException e) {
            throw new RuntimeException("Could not create transactions directory", e);
        }
    }

    private Path getUserFilePath(final String userId) {
        return transactionsDirectory.resolve("transactions_" + userId + ".csv");
    }

    @Override
    public void save(final String userId, final Transaction transaction) {
        final List<Transaction> all = loadUserTransactions(userId);
        all.add(transaction);
        writeUserTransactions(userId, all);
    }

    @Override
    public List<Transaction> getByPortfolio(final String userId, final String portfolioId) {
        return loadUserTransactions(userId)
            .stream()
            .filter(tx ->
                portfolioId.equals(tx.getFromPortfolio()) ||
                    portfolioId.equals(tx.getToPortfolio()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getByFilters(final String userId,
                                          final String portfolioId,
                                          final String assetSymbol,
                                          final LocalDate startDate,
                                          final LocalDate endDate) {
        return loadUserTransactions(userId)
            .stream()
            .filter(tx ->
                portfolioId.equals(tx.getFromPortfolio()) ||
                    portfolioId.equals(tx.getToPortfolio()))
            .filter(tx -> {
                if (assetSymbol == null || assetSymbol.isBlank()) {
                    return true;
                }
                // Only Buy/Sell have asset symbols
                if (tx instanceof BuyTransaction) {
                    final BuyTransaction bt = (BuyTransaction) tx;
                    return assetSymbol.equalsIgnoreCase(bt.getAssetSymbol());
                }
                else if (tx instanceof SellTransaction) {
                    final SellTransaction st = (SellTransaction) tx;
                    return assetSymbol.equalsIgnoreCase(st.getAssetSymbol());
                }
                return false;
            })

            .filter(tx -> {
                final LocalDate d = tx.getDate().toLocalDate();
                if (startDate != null && d.isBefore(startDate)) {
                    return false;
                }
                return endDate == null || !d.isAfter(endDate);
            })
            .collect(Collectors.toList());
    }

    // ---------- internal helpers ----------

    private List<Transaction> loadUserTransactions(final String userId) {
        final Path file = getUserFilePath(userId);
        final List<Transaction> result = new ArrayList<>();

        if (!Files.exists(file)) {
            return result; // no transactions yet
        }

        try {
            final List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            boolean first = true;
            for (final String line : lines) {
                if (first) {
                    first = false;
                    if (line.startsWith("transactionId,")) {
                        // header line, skip
                        continue;
                    }
                }
                if (line.isBlank()) {
                    continue;
                }
                final Transaction tx = parseCsvLine(line);
                if (tx != null) {
                    result.add(tx);
                }
            }
        }
        catch (final IOException e) {
            throw new RuntimeException("Failed to read transactions for user " + userId, e);
        }

        return result;
    }

    private void writeUserTransactions(final String userId, final List<Transaction> transactions) {
        final Path file = getUserFilePath(userId);
        final List<String> lines = new ArrayList<>();
        // header
        lines.add("transactionId,dateTime,fromPortfolio,toPortfolio,transactionType," +
            "assetType,assetSymbol,quantity,pricePerUnit,totalValue");

        for (final Transaction tx : transactions) {
            lines.add(toCsvLine(tx));
        }

        try {
            Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        }
        catch (final IOException e) {
            throw new RuntimeException("Failed to write transactions for user " + userId, e);
        }
    }

    /**
     * Parse a CSV line into the correct Transaction subclass.
     */
    private Transaction parseCsvLine(final String line) {
        final String[] parts = line.split(",", -1); // keep empty fields

        final String transactionId = parts[0];
        final LocalDateTime dateTime = LocalDateTime.parse(parts[1]);
        final String fromPortfolio = emptyToNull(parts[2]);
        final String toPortfolio = emptyToNull(parts[3]);
        final String type = parts[4];

        final String assetType = emptyToNull(parts[5]);
        final String assetSymbol = emptyToNull(parts[6]);
        final double quantity = parseDoubleSafe(parts[7]);
        final double pricePerUnit = parseDoubleSafe(parts[8]);
        // parts[9] is totalValue, but we can recompute in the constructor

        return switch (type) {
            case "BUY" -> new BuyTransaction(
                transactionId,
                dateTime,
                toPortfolio,      // portfolio receiving the asset
                assetType,
                assetSymbol,
                quantity,
                pricePerUnit
            );
            case "SELL" -> new SellTransaction(
                transactionId,
                dateTime,
                fromPortfolio,    // portfolio selling the asset
                assetType,
                assetSymbol,
                quantity,
                pricePerUnit
            );
            default -> null; // TODO: later handle TRANSFER / CONVERT
        };
    }

    private String toCsvLine(final Transaction tx) {
        final String transactionType = tx.getTransactionType();

        String assetType = "";
        String assetSymbol = "";
        double quantity = 0;
        double pricePerUnit = 0;
        double totalValue = 0;

        if (tx instanceof BuyTransaction) {
            final BuyTransaction bt = (BuyTransaction) tx;
            assetType = nullToEmpty(bt.getAssetType());
            assetSymbol = nullToEmpty(bt.getAssetSymbol());
            quantity = bt.getQuantity();
            pricePerUnit = bt.getPricePerUnit();
            totalValue = bt.getTotalValue();
        }
        else if (tx instanceof SellTransaction) {
            final SellTransaction st = (SellTransaction) tx;
            assetType = nullToEmpty(st.getAssetType());
            assetSymbol = nullToEmpty(st.getAssetSymbol());
            quantity = st.getQuantity();
            pricePerUnit = st.getPricePerUnit();
            totalValue = st.getTotalValue();
        }


        return String.join(",",
            nullToEmpty(tx.getTransactionId()),
            tx.getDate().toString(),
            nullToEmpty(tx.getFromPortfolio()),
            nullToEmpty(tx.getToPortfolio()),
            transactionType,
            assetType,
            assetSymbol,
            Double.toString(quantity),
            Double.toString(pricePerUnit),
            Double.toString(totalValue)
        );
    }

    private String nullToEmpty(final String s) {
        return s == null ? "" : s;
    }

    private String emptyToNull(final String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

    private double parseDoubleSafe(final String s) {
        if (s == null || s.isBlank()) {
            return 0.0;
        }
        return Double.parseDouble(s);
    }
}