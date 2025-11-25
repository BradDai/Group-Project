package use_case.transfer;

/**
 * Output Boundary for the Transfer Use Case.
 */
public interface TransferOutputBoundary {

    /**
     * Prepares the success view for the Transfer Use Case.
     *
     * @param outputData the output data
     */
    void prepareSuccessView(TransferOutputData outputData);

    /**
     * Prepares the failure view for the Transfer Use Case.
     *
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

    /**
     * Presents the balances for both portfolios, including available currencies and stocks.
     *
     * @param fromBalance  the balance of the source portfolio
     * @param toBalance    the balance of the destination portfolio
     * @param currencyList the list of currencies available in the destination portfolio
     * @param stockList    the list of stocks available in the destination portfolio
     */
    void presentBalances(double fromBalance, double toBalance, String[] currencyList, String[] stockList);

    /**
     * Presents the balances for both portfolios, including the currencies available
     * for currency transfers.
     *
     * @param fromBalance         the balance of the source portfolio
     * @param toBalance           the balance of the destination portfolio
     * @param availableCurrencies the list of currencies available for the transfer type
     */
    void presentBalances(double fromBalance, double toBalance, String[] availableCurrencies);
}
