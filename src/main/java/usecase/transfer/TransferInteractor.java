package usecase.transfer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import dataaccess.TransactionDataAccessInterface;
import entity.SubAccount;
import entity.transaction.TransferTransaction;
import entity.transaction.TransferTransactionBuilder;

public class TransferInteractor implements TransferInputBoundary {
    private final TransferDataAccessInterface transferDataAccess;
    private final TransferOutputBoundary transferPresenter;
    private final TransferTransactionBuilder transactionBuilder;
    private final TransactionDataAccessInterface transactionRepo;

    public TransferInteractor(final TransferDataAccessInterface transferDataAccess,
                              final TransferOutputBoundary transferPresenter,
                              final TransactionDataAccessInterface transactionRepo) {
        this.transferDataAccess = transferDataAccess;
        this.transferPresenter = transferPresenter;
        this.transactionBuilder = new TransferTransactionBuilder();
        this.transactionRepo = transactionRepo;
    }

    @Override
    public void execute(final TransferInputData transferInputData) {
        final String username = transferInputData.username();
        final String fromPortfolio = transferInputData.fromPortfolio();
        final String toPortfolio = transferInputData.toPortfolio();
        final String transferType = transferInputData.transferType();
        final String assetSymbol = transferInputData.assetSymbol();
        final double amount = transferInputData.amount();

        if (!transferDataAccess.portfolioExists(username, fromPortfolio)) {
            transferPresenter.prepareFailView("Source portfolio does not exist: " + fromPortfolio);
        }
        else if (!transferDataAccess.portfolioExists(username, toPortfolio)) {
            transferPresenter.prepareFailView("Destination portfolio does not exist: " + toPortfolio);
        }
        else if (fromPortfolio.equals(toPortfolio)) {
            transferPresenter.prepareFailView("Cannot transfer to the same portfolio");
        }
        else if (!transferDataAccess.hasAsset(username, fromPortfolio, assetSymbol)) {
            transferPresenter.prepareFailView("Source portfolio does not contain asset: " + assetSymbol);
        }
        else {
            final double availableBalance =
                transferDataAccess.getAssetBalance(username, fromPortfolio, assetSymbol);
            if (availableBalance < amount) {
                transferPresenter.prepareFailView(
                    String.format("Insufficient balance. Available: %.2f", availableBalance));
            }
            else {
                transferDataAccess.transferAsset(
                    username, fromPortfolio, toPortfolio, assetSymbol, amount);

                final String transactionId = UUID.randomUUID().toString();
                final TransferTransaction transaction = transactionBuilder
                    .setTransactionId(transactionId)
                    .setDate(LocalDateTime.now())
                    .setFromPortfolio(fromPortfolio)
                    .setToPortfolio(toPortfolio)
                    .setAssetType(transferType)
                    .setAssetSymbol(assetSymbol)
                    .setQuantity(amount)
                    .build();

                transactionRepo.save(username, transaction);

                final List<SubAccount> updatedAccounts =
                    transferDataAccess.getSubAccountsOf(username);
                final TransferOutputData outputData = new TransferOutputData(
                    transactionId, fromPortfolio, toPortfolio,
                    assetSymbol, amount, true, updatedAccounts);

                transferPresenter.prepareSuccessView(outputData);
            }
        }
    }

    @Override
    public void checkBalances(
        final String username, final String fromPortfolio,
        final String toPortfolio, final String assetSymbol) {

        double fromBalance = 0.0;
        double toBalance = 0.0;
        String[] currencies = new String[] {"USD"};
        String[] stocks = new String[0];

        if (transferDataAccess.portfolioExists(username, fromPortfolio)) {
            fromBalance = transferDataAccess.getAssetBalance(username, fromPortfolio, assetSymbol);
            currencies = transferDataAccess.getAvailableCurrencies(username, fromPortfolio);
            stocks = transferDataAccess.getAvailableStocks(username, fromPortfolio);
        }

        if (transferDataAccess.portfolioExists(username, toPortfolio)) {
            toBalance = transferDataAccess.getAssetBalance(username, toPortfolio, assetSymbol);
        }

        transferPresenter.presentBalances(fromBalance, toBalance, currencies, stocks);
    }
}

