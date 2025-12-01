package entity.transaction;

/**
 * Utility class to safely extract portfolio information from Transaction objects.
 * This bridges the gap between the strict LSP-compliant entities and the
 * data access/interactor layers that need to treat them generically.
 */
public class TransactionUtility {

    /**
     * Safely extracts the 'source' portfolio from a transaction.
     * @param tx The transaction to inspect.
     * @return The name of the source portfolio, or null if not applicable.
     */
    public static String getFromPortfolio(Transaction tx) {
        if (tx instanceof SellTransaction) {
            return ((SellTransaction) tx).getFromPortfolio();
        } else if (tx instanceof TransferTransaction) {
            return ((TransferTransaction) tx).getFromPortfolio();
        } else if (tx instanceof ConvertTransaction) {
            return ((ConvertTransaction) tx).getPortfolio();
        }
        return null;
    }

    /**
     * Safely extracts the 'destination' portfolio from a transaction.
     * @param tx The transaction to inspect.
     * @return The name of the destination portfolio, or null if not applicable.
     */
    public static String getToPortfolio(Transaction tx) {
        if (tx instanceof BuyTransaction) {
            return ((BuyTransaction) tx).getToPortfolio();
        } else if (tx instanceof TransferTransaction) {
            return ((TransferTransaction) tx).getToPortfolio();
        } else if (tx instanceof ConvertTransaction) {
            return ((ConvertTransaction) tx).getPortfolio();
        }
        return null;
    }
}
