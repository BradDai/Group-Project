package data_access;

import entity.transaction.BuyTransaction;
import entity.transaction.SellTransaction;
import entity.transaction.Transaction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public FileTransactionDataAccess(String directoryPath) {
        this.transactionsDirectory = Paths.get(directoryPath);
        try {
            if (!Files.exists(transactionsDirectory)) {
                Files.createDirectories(transactionsDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create transactions directory", e);
        }
    }

    private Path getUserFilePath(String userId) {
        return transactionsDirectory.resolve("transactions_" + userId + ".csv");
    }

    @Override
    public void save(String userId, Transaction transaction) {
        List<Transaction> all = loadUserTransactions(userId);
        all.add(transaction);
        writeUserTransactions(userId, all);
    }

    @Override
    public List<Transaction> getByPortfolio(String userId, String portfolioId) {
        return loadUserTransactions(userId)
                .stream()
                .filter(tx ->
                        portfolioId.equals(tx.getFromPortfolio()) ||
                                portfolioId.equals(tx.getToPortfolio()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getByFilters(String userId,
                                          String portfolioId,
                                          String assetSymbol,
                                          LocalDate startDate,
                                          LocalDate endDate) {
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
                        BuyTransaction bt = (BuyTransaction) tx;
                        return assetSymbol.equalsIgnoreCase(bt.getAssetSymbol());
                    } else if (tx instanceof SellTransaction) {
                        SellTransaction st = (SellTransaction) tx;
                        return assetSymbol.equalsIgnoreCase(st.getAssetSymbol());
                    }
                    return false;
                })

                .filter(tx -> {
                    LocalDate d = tx.getDate().toLocalDate();
                    if (startDate != null && d.isBefore(startDate)) return false;
                    if (endDate != null && d.isAfter(endDate)) return false;
                    return true;
                })
                .collect(Collectors.toList());
    }

    // ---------- internal helpers ----------

    private List<Transaction> loadUserTransactions(String userId) {
        Path file = getUserFilePath(userId);
        List<Transaction> result = new ArrayList<>();

        if (!Files.exists(file)) {
            return result; // no transactions yet
        }

        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            boolean first = true;
            for (String line : lines) {
                if (first) {
                    first = false;
                    if (line.startsWith("transactionId,")) {
                        // header line, skip
                        continue;
                    }
                }
                if (line.isBlank()) continue;
                Transaction tx = parseCsvLine(line);
                if (tx != null) {
                    result.add(tx);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read transactions for user " + userId, e);
        }

        return result;
    }

    private void writeUserTransactions(String userId, List<Transaction> transactions) {
        Path file = getUserFilePath(userId);
        List<String> lines = new ArrayList<>();
        // header
        lines.add("transactionId,dateTime,fromPortfolio,toPortfolio,transactionType," +
                "assetType,assetSymbol,quantity,pricePerUnit,totalValue");

        for (Transaction tx : transactions) {
            lines.add(toCsvLine(tx));
        }

        try {
            Files.write(file, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write transactions for user " + userId, e);
        }
    }

    /**
     * Parse a CSV line into the correct Transaction subclass.
     */
    private Transaction parseCsvLine(String line) {
        String[] parts = line.split(",", -1); // keep empty fields

        String transactionId = parts[0];
        LocalDateTime dateTime = LocalDateTime.parse(parts[1]);
        String fromPortfolio = emptyToNull(parts[2]);
        String toPortfolio = emptyToNull(parts[3]);
        String type = parts[4];

        String assetType = emptyToNull(parts[5]);
        String assetSymbol = emptyToNull(parts[6]);
        double quantity = parseDoubleSafe(parts[7]);
        double pricePerUnit = parseDoubleSafe(parts[8]);
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

    private String toCsvLine(Transaction tx) {
        String transactionType = tx.getTransactionType();

        String assetType = "";
        String assetSymbol = "";
        double quantity = 0;
        double pricePerUnit = 0;
        double totalValue = 0;

        if (tx instanceof BuyTransaction) {
            BuyTransaction bt = (BuyTransaction) tx;
            assetType = nullToEmpty(bt.getAssetType());
            assetSymbol = nullToEmpty(bt.getAssetSymbol());
            quantity = bt.getQuantity();
            pricePerUnit = bt.getPricePerUnit();
            totalValue = bt.getTotalValue();
        } else if (tx instanceof SellTransaction) {
            SellTransaction st = (SellTransaction) tx;
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

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

    private double parseDoubleSafe(String s) {
        if (s == null || s.isBlank()) return 0.0;
        return Double.parseDouble(s);
    }
}
