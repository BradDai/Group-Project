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
     */
    public void executeStockTransfer(String username, String fromPortfolio, String toPortfolio,
                                     String symbol, int amount) {
        final TransferInputData transferInputData = new TransferInputData(
                username, fromPortfolio, toPortfolio, "Stock", symbol, amount);

        transferInteractor.execute(transferInputData);
    }

    /**
     * Executes the currency transfer use case.
     */
    public void executeCurrencyTransfer(String username, String fromPortfolio, String toPortfolio,
                                        String currency, double amount) {
        final TransferInputData transferInputData = new TransferInputData(
                username, fromPortfolio, toPortfolio, "Currency", currency, amount);

        transferInteractor.execute(transferInputData);
    }

    public void checkBalances(String username, String fromPortfolio, String toPortfolio, String assetSymbol) {
        transferInteractor.checkBalances(username, fromPortfolio, toPortfolio, assetSymbol);
    }
}