package interfaceadapter.transfer;

import usecase.transfer.TransferInputBoundary;
import usecase.transfer.TransferInputData;

/**
 * Controller for the Transfer Use Case.
 * This class receives UI-layer input, constructs {@link TransferInputData},
 * and forwards it to the use case interactor.
 */
public class TransferController {
    private final TransferInputBoundary transferInteractor;

    /**
     * Creates a new {@code TransferController}.
     *
     * @param transferInteractor the interactor responsible for executing the transfer use case
     */
    public TransferController(final TransferInputBoundary transferInteractor) {
        this.transferInteractor = transferInteractor;
    }

    /**
     * Executes the stock transfer use case.
     *
     * @param username      the user performing the transfer
     * @param fromPortfolio the portfolio the stock is transferred from
     * @param toPortfolio   the portfolio the stock is transferred to
     * @param symbol        the stock ticker symbol being transferred
     * @param amount        the quantity of shares being transferred
     */
    public void executeStockTransfer(final String username, final String fromPortfolio,
                                     final String toPortfolio, final String symbol,
                                     final int amount) {
        final TransferInputData transferInputData =
            new TransferInputData(username, fromPortfolio, toPortfolio, "Stock", symbol, amount);
        transferInteractor.execute(transferInputData);
    }

    /**
     * Executes the currency transfer use case.
     *
     * @param username      the user performing the transfer
     * @param fromPortfolio the portfolio the currency is transferred from
     * @param toPortfolio   the portfolio the currency is transferred to
     * @param currency      the currency code (e.g., "USD")
     * @param amount        the amount of currency to transfer
     */
    public void executeCurrencyTransfer(final String username, final String fromPortfolio,
                                        final String toPortfolio, final String currency,
                                        final double amount) {
        final TransferInputData transferInputData =
            new TransferInputData(username, fromPortfolio, toPortfolio, "Currency", currency, amount);
        transferInteractor.execute(transferInputData);
    }

    /**
     * Requests updated balances for the selected portfolios and asset.
     *
     * @param username      the user requesting the balance check
     * @param fromPortfolio the source portfolio
     * @param toPortfolio   the destination portfolio
     * @param assetSymbol   the stock symbol or currency code whose balance should be checked
     */
    public void checkBalances(final String username, final String fromPortfolio, final String toPortfolio,
                              final String assetSymbol) {
        transferInteractor.checkBalances(username, fromPortfolio, toPortfolio, assetSymbol);
    }
}
