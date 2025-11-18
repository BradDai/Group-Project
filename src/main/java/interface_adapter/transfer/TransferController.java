package interface_adapter.transfer;

import use_case.transfer.TransferInputBoundary;
import use_case.transfer.TransferInputData;

/**
 * Controller for the Transfer Use Case.
 */
public class TransferController {
    private final TransferInputBoundary transferInteractor;

    public TransferController(TransferInputBoundary transferInteractor) {
        this.transferInteractor = transferInteractor;
    }

    /**
     * Executes the stock transfer use case.
     * @param fromPortfolio source portfolio
     * @param toPortfolio destination portfolio
     * @param symbol stock symbol
     * @param amount amount of stock to transfer
     */
    public void executeStockTransfer(String fromPortfolio, String toPortfolio,
                                     String symbol, int amount) {
        final TransferInputData transferInputData = new TransferInputData(
                fromPortfolio, toPortfolio, "Stock", symbol, amount);

        transferInteractor.execute(transferInputData);
    }

    /**
     * Executes the currency transfer use case.
     * @param fromPortfolio source portfolio
     * @param toPortfolio destination portfolio
     * @param currency currency type
     * @param amount amount of currency to transfer
     */
    public void executeCurrencyTransfer(String fromPortfolio, String toPortfolio,
                                        String currency, double amount) {
        final TransferInputData transferInputData = new TransferInputData(
                fromPortfolio, toPortfolio, "Currency", currency, amount);

        transferInteractor.execute(transferInputData);
    }
}
