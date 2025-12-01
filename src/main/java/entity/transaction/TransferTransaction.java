package entity.transaction;

import java.time.LocalDateTime;

public class TransferTransaction extends Transaction {
    private final String fromPortfolio;
    private final String toPortfolio;
    private final String assetType;
    private final String assetSymbol;
    private final double quantity;

    public TransferTransaction(final String transactionId, final LocalDateTime date,
                               final String fromPortfolio, final String toPortfolio,
                               final String assetType, final String assetSymbol, final double quantity) {
        super(transactionId, date);

        this.fromPortfolio = fromPortfolio;
        this.toPortfolio = toPortfolio;
        this.assetType = assetType;
        this.assetSymbol = assetSymbol;
        this.quantity = quantity;
    }

    @Override
    public String getTransactionType() {
        return "TRANSFER";
    }

    @Override
    public String getDescription() {
        return String.format("Transfer of %.2f %s from %s to %s",
            quantity, assetSymbol, fromPortfolio, toPortfolio);
    }

    @Override
    public String getAssetSymbol() {
        return assetSymbol;
    }

    @Override
    public double getQuantity() {
        return quantity;
    }

    public String getFromPortfolio() {
        return fromPortfolio;
    }

    public String getToPortfolio() {
        return toPortfolio;
    }

    public String getAssetType() {
        return assetType;
    }
}
