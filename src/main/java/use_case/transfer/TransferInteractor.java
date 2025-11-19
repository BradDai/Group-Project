package use_case.transfer;

import entity.transaction.TransferTransaction;
import entity.transaction.TransferTransactionBuilder;
import java.time.LocalDateTime;
import java.util.UUID;

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
        final String username = transferInputData.getUsername();
        final String fromPortfolio = transferInputData.getFromPortfolio();
        final String toPortfolio = transferInputData.getToPortfolio();
        final String transferType = transferInputData.getTransferType();
        final String assetSymbol = transferInputData.getAssetSymbol();
        final double amount = transferInputData.getAmount();

        if (!transferDataAccess.portfolioExists(username, fromPortfolio)) {
            transferPresenter.prepareFailView("Source portfolio does not exist: " + fromPortfolio);
            return;
        }
        if (!transferDataAccess.portfolioExists(username, toPortfolio)) {
            transferPresenter.prepareFailView("Destination portfolio does not exist: " + toPortfolio);
            return;
        }
        if (fromPortfolio.equals(toPortfolio)) {
            transferPresenter.prepareFailView("Cannot transfer to the same portfolio");
            return;
        }
        if (!transferDataAccess.hasAsset(username, fromPortfolio, assetSymbol)) {
            transferPresenter.prepareFailView("Source portfolio does not contain asset: " + assetSymbol);
            return;
        }

        double availableBalance = transferDataAccess.getAssetBalance(username, fromPortfolio, assetSymbol);
        if (availableBalance < amount) {
            transferPresenter.prepareFailView(String.format("Insufficient balance. Available: %.2f", availableBalance));
            return;
        }

        try {
            transferDataAccess.transferAsset(username, fromPortfolio, toPortfolio, assetSymbol, amount);

            String transactionId = UUID.randomUUID().toString();
            TransferTransaction transaction = transactionBuilder
                    .setTransactionId(transactionId)
                    .setDate(LocalDateTime.now())
                    .setFromPortfolio(fromPortfolio)
                    .setToPortfolio(toPortfolio)
                    .setAssetType(transferType)
                    .setAssetSymbol(assetSymbol)
                    .setQuantity(amount)
                    .build();

            transferDataAccess.saveTransaction(transaction);
            final TransferOutputData outputData = new TransferOutputData(
                    transactionId, fromPortfolio, toPortfolio, assetSymbol, amount, true);

            transferPresenter.prepareSuccessView(outputData);
        } catch (Exception e) {
            transferPresenter.prepareFailView("Transfer failed: " + e.getMessage());
        }
    }

    @Override
    public void checkBalances(String username, String fromPortfolio, String toPortfolio, String assetSymbol) {
        double fromBalance = 0.0;
        double toBalance = 0.0;

        if (transferDataAccess.portfolioExists(username, fromPortfolio)) {
            fromBalance = transferDataAccess.getAssetBalance(username, fromPortfolio, assetSymbol);
        }

        if (transferDataAccess.portfolioExists(username, toPortfolio)) {
            toBalance = transferDataAccess.getAssetBalance(username, toPortfolio, assetSymbol);
        }

        transferPresenter.presentBalances(fromBalance, toBalance);
    }
}