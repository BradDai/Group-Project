package entity;

public class Stock extends Asset {
    private final String companySymbol;

    public Stock(final String type, final double quantity, final String companySymbol) {
        super(type, quantity);
        this.companySymbol = companySymbol;
    }

    public String getCompanySymbol() {
        return companySymbol;
    }

    // NEW: Add business logic methods

    /**
     * Return TRUE if the stock can be sold for the given quantity.
     * @param quantityToSell the quantity to sell.
     * @return TRUE if quantity to sell is less or equal to the current quantity.
     */
    public boolean canSell(final double quantityToSell) {
        return quantityToSell > 0 && quantityToSell <= getQuantity();
    }

    /**
     * Sell the given quantity of stocks.
     * @param quantityToSell quantity to sell
     * @throws IllegalArgumentException if the quantity is invalid or exceeds current holdings
     */
    public void sell(final double quantityToSell) {
        if (!canSell(quantityToSell)) {
            throw new IllegalArgumentException("Cannot sell " + quantityToSell + " units");
        }
        setQuantity(getQuantity() - quantityToSell);
    }

    public boolean isEmpty() {
        return getQuantity() == 0;
    }

    /**
     * Calculate the total price.
     * @param pricePerUnit price of one stock
     * @return total price calculated
     */
    public double calculateValue(final double pricePerUnit) {
        return getQuantity() * pricePerUnit;
    }
}
