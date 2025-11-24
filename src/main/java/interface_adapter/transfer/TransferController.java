package interface_adapter.transfer;

import use_case.transfer.TransferInputBoundary;
import use_case.transfer.TransferInputData;

/**
 * Controller for the Transfer Use Case.
 */
public class TransferController {
    private final TransferInputBoundary transferInteractor;

    public TransferController(final TransferInputBoundary transferInteractor) {
        this.transferInteractor = transferInteractor;
    }

    /**
     * Executes the stock transfer use case.
     */
    public void executeStockTransfer(final String username, final String fromPortfolio, final String toPortfolio,
                                     final String symbol, final int amount) {
        final TransferInputData transferInputData = new TransferInputData(
            username, fromPortfolio, toPortfolio, "Stock", symbol, amount);

        transferInteractor.execute(transferInputData);
    }

    /**
     * Executes the currency transfer use case.
     */
    public void executeCurrencyTransfer(final String username, final String fromPortfolio, final String toPortfolio,
                                        final String currency, final double amount) {
        final TransferInputData transferInputData = new TransferInputData(
            username, fromPortfolio, toPortfolio, "Currency", currency, amount);

        transferInteractor.execute(transferInputData);
    }

    public void checkBalances(final String username, final String fromPortfolio, final String toPortfolio, final String assetSymbol) {
        transferInteractor.checkBalances(username, fromPortfolio, toPortfolio, assetSymbol);
    }
}
