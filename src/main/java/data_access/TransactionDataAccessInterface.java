package data_access;

import entity.transaction.Transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access interface for reading/writing transactions.
 * We store one file per user.
 */
public interface TransactionDataAccessInterface {

    /**
     * Save a new transaction for the given user.
     *
     * @param userId      the user who owns the transaction
     * @param transaction the transaction to save
     */
    void save(String userId, Transaction transaction);

    /**
     * Get all transactions related to a specific portfolio for a user.
     * Returns transactions where this portfolio is either the source
     * (fromPortfolio) or destination (toPortfolio).
     */
    List<Transaction> getByPortfolio(String userId, String portfolioId);

    /**
     * Get transactions for a user with optional filters.
     *
     * @param userId      owner of the transactions
     * @param portfolioId required portfolio id
     * @param assetSymbol optional asset symbol filter (null/empty = any)
     * @param startDate   optional; if not null, include only dates >= startDate
     * @param endDate     optional; if not null, include only dates <= endDate
     */
    List<Transaction> getByFilters(String userId,
                                   String portfolioId,
                                   String assetSymbol,
                                   LocalDate startDate,
                                   LocalDate endDate);
}