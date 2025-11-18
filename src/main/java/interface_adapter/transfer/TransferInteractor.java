package interface_adapter.transfer;

import entity.*;
import entity.transaction.Transaction;
import entity.transaction.TransferTransaction;
import entity.transaction.TransferTransactionBuilder;
import interface_adapter.transfer.*;
import use_case.transfer.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The Transfer Interactor.
 */
public class TransferInteractor implements TransferInputBoundary {
    private final TransferDataAccessInterface transferDataAccess;
    private final TransferOutputBoundary transferPresenter;
    private final TransferTransactionBuilder transactionBuilder;

    public TransferInteractor(TransferDataAccessInterface transferDataAccess,
                              TransferOutputBoundary transferPresenter) {
        this.transferDataAccess = transferDataAccess;
        this.transferPresenter = transferPresenter;
        this.transactionBuilder = new TransferTransactionBuilder();
    }

    @Override
    public void execute(TransferInputData transferInputData) {
        final String fromPortfolio = transferInputData.getFromPortfolio();
        final String toPortfolio = transferInputData.getToPortfolio();
        final String transferType = transferInputData.getTransferType();
        final String assetSymbol = transferInputData.getAssetSymbol();
        final double amount = transferInputData.getAmount();

        // Validate portfolios exist
        if (!transferDataAccess.portfolioExists(fromPortfolio)) {
            transferPresenter.prepareFailView("Source portfolio does not exist: " + fromPortfolio);
            return;
        }

        if (!transferDataAccess.portfolioExists(toPortfolio)) {
            transferPresenter.prepareFailView("Destination portfolio does not exist: " + toPortfolio);
            return;
        }

        // Validate same portfolio check
        if (fromPortfolio.equals(toPortfolio)) {
            transferPresenter.prepareFailView("Cannot transfer to the same portfolio");
            return;
        }

        // Check if source portfolio has the asset
        if (!transferDataAccess.hasAsset(fromPortfolio, assetSymbol)) {
            transferPresenter.prepareFailView("Source portfolio does not contain asset: " + assetSymbol);
            return;
        }

        // Check if source portfolio has sufficient balance
        double availableBalance = transferDataAccess.getAssetBalance(fromPortfolio, assetSymbol);
        if (availableBalance < amount) {
            transferPresenter.prepareFailView(
                    String.format("Insufficient balance. Available: %.2f, Requested: %.2f",
                            availableBalance, amount));
            return;
        }

        try {
            // Execute the transfer
            transferDataAccess.transferAsset(fromPortfolio, toPortfolio, assetSymbol, amount);

            // Create transaction record
            String transactionId = UUID.randomUUID().toString();
            TransferTransaction transaction = transactionBuilder
                    .setTransactionId(transactionId)
                    .setDate(LocalDateTime.now())
                    .setFromPortfolio(Integer.parseInt(fromPortfolio))
                    .setToPortfolio(Integer.parseInt(toPortfolio))
                    .setAssetType(transferType)
                    .setAssetSymbol(assetSymbol)
                    .setQuantity(amount)
                    .build();

            // Save transaction to history
            transferDataAccess.saveTransaction(transaction);

            // Prepare success response
            final TransferOutputData outputData = new TransferOutputData(
                    transactionId, fromPortfolio, toPortfolio, assetSymbol, amount, true);

            transferPresenter.prepareSuccessView(outputData);

        } catch (Exception e) {
            transferPresenter.prepareFailView("Transfer failed: " + e.getMessage());
        }
    }
}
