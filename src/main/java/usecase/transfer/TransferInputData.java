package usecase.transfer;

/**
 * Input Data for the Transfer Use Case.
 *
 * @param username      the username of the account performing the transfer
 * @param fromPortfolio the name of the source portfolio
 * @param toPortfolio   the name of the destination portfolio
 * @param transferType  the type of transfer (e.g., currency or stock)
 * @param assetSymbol   the symbol of the asset being transferred
 * @param amount        the amount of the asset to transfer
 */
public record TransferInputData(
    String username,
    String fromPortfolio,
    String toPortfolio,
    String transferType,
    String assetSymbol,
    double amount
) {
}
