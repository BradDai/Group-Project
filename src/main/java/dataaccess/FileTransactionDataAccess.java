package dataaccess;

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
import entity.transaction.ConvertTransaction;
import entity.transaction.SellTransaction;
import entity.transaction.Transaction;
import entity.transaction.TransferTransaction;
import entity.transaction.TransferTransactionBuilder;

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
                                          final String assetSymbol,   // may be null
                                          final LocalDate startDate,
                                          final LocalDate endDate) {

        return loadUserTransactions(userId)
            .stream()
            // portfolio filter
            .filter(tx ->
                portfolioId.equals(tx.getFromPortfolio()) ||
                    portfolioId.equals(tx.getToPortfolio()))
            // asset filter (safe when assetSymbol or tx asset is null)
            .filter(tx -> {
                // if the caller didn't filter by asset, accept everything
                if (assetSymbol == null || assetSymbol.isBlank()) {
                    return true;
                }

                String txAsset = null;

                if (tx instanceof final BuyTransaction bt) {
                    txAsset = bt.getAssetSymbol();
                }
                else if (tx instanceof final SellTransaction st) {
                    txAsset = st.getAssetSymbol();
                }
                else if (tx instanceof final ConvertTransaction ct) {
                    // store "FROM->TO" for currency conversions
                    txAsset = ct.getFromCurrency() + "->" + ct.getToCurrency();
                }
                else if (tx instanceof final TransferTransaction tt) {
                    txAsset = tt.getAssetSymbol();
                }

                // if transaction has no asset symbol, it can't match the filter
                if (txAsset == null || txAsset.isBlank()) {
                    return false;
                }

                // compare transaction asset with the filter string
                return txAsset.equalsIgnoreCase(assetSymbol);
            })
            // date filter
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
            case "CONVERT" -> {
                // assetSymbol stored as "FROM->TO"
                String fromCurrency = null;
                String toCurrency = null;
                if (assetSymbol != null && assetSymbol.contains("->")) {
                    final String[] curr = assetSymbol.split("->", 2);
                    fromCurrency = curr[0];
                    toCurrency = curr[1];
                }

                yield new ConvertTransaction(
                    transactionId,
                    dateTime,
                    fromPortfolio != null ? fromPortfolio : toPortfolio,
                    fromCurrency,
                    toCurrency,
                    quantity,
                    pricePerUnit
                );
            }
            case "TRANSFER" -> {
                final TransferTransactionBuilder builder = new TransferTransactionBuilder();
                yield builder
                    .setTransactionId(transactionId)
                    .setDate(dateTime)
                    .setFromPortfolio(fromPortfolio)
                    .setToPortfolio(toPortfolio)
                    .setAssetType(assetType)
                    .setAssetSymbol(assetSymbol)
                    .setQuantity(quantity)
                    .build();
            }
            default -> null; // unknown types are ignored
        };
    }

    private String toCsvLine(final Transaction tx) {
        final String transactionType = tx.getTransactionType();

        String assetType = "";
        String assetSymbol = "";
        double quantity = 0;
        double pricePerUnit = 0;
        double totalValue = 0;

        if (tx instanceof final BuyTransaction bt) {
            assetType = nullToEmpty(bt.getAssetType());
            assetSymbol = nullToEmpty(bt.getAssetSymbol());
            quantity = bt.getQuantity();
            pricePerUnit = bt.getPricePerUnit();
            totalValue = bt.getTotalValue();
        }
        else if (tx instanceof final SellTransaction st) {
            assetType = nullToEmpty(st.getAssetType());
            assetSymbol = nullToEmpty(st.getAssetSymbol());
            quantity = st.getQuantity();
            pricePerUnit = st.getPricePerUnit();
            totalValue = st.getTotalValue();
        }
        else if (tx instanceof final ConvertTransaction ct) {
            // Represent currency conversion as a pair
            assetType = "CURRENCY";
            assetSymbol = ct.getFromCurrency() + "->" + ct.getToCurrency();
            quantity = ct.getFromAmount();        // source amount
            pricePerUnit = ct.getExchangeRate();  // FX rate
            totalValue = ct.getToAmount();
        }
        else if (tx instanceof final TransferTransaction tt) {
            assetType = nullToEmpty(tt.getAssetType());
            assetSymbol = nullToEmpty(tt.getAssetSymbol());
            quantity = tt.getQuantity();
            // transfer typically doesn’t have a price/total; leave them as 0
            pricePerUnit = 0.0;
            totalValue = 0.0;
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
