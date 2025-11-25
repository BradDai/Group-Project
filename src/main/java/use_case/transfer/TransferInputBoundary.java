package use_case.transfer;

import interface_adapter.transfer.TransferException;

/**
 * Input Boundary for the Transfer Use Case.
 * Defines the operations that can be performed for transferring assets
 * between portfolios, including executing a transfer and checking balances.
 */
public interface TransferInputBoundary {

    /**
     * Executes the transfer use case.
     *
     * @param transferInputData the input data containing all transfer details
     * @throws TransferException if the transfer cannot be completed due to business logic or validation errors
     */
    void execute(TransferInputData transferInputData) throws TransferException;

    /**
     * Checks the balances of the specified portfolios for the given asset.
     *
     * @param username      the user performing the transfer
     * @param fromPortfolio the portfolio to transfer from
     * @param toPortfolio   the portfolio to transfer to
     * @param assetSymbol   the symbol of the asset being checked
     */
    void checkBalances(String username, String fromPortfolio, String toPortfolio, String assetSymbol);
}
