package use_case.buyasset;

import entity.Stock;
import entity.SubAccount;
import use_case.SubAccount.SubAccountDataAccessInterface;

import java.math.BigDecimal;
import java.util.List;

public class BuyAssetInteractor implements BuyAssetInputBoundary {

    private final SubAccountDataAccessInterface subAccountDAO;
    private final BuyAssetOutputBoundary presenter;

    public BuyAssetInteractor(SubAccountDataAccessInterface subAccountDAO,
                              BuyAssetOutputBoundary presenter) {
        this.subAccountDAO = subAccountDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(BuyAssetInputData inputData) {

        String username = inputData.getUsername();
        String portfolioName = inputData.getPortfolioName();
        String symbol = inputData.getSymbol();
        int qty = inputData.getQuantity();
        double price = inputData.getPrice();

        if (username == null || username.isEmpty()) {
            presenter.presentFail("No user logged in.");
            return;}
        if (portfolioName == null || portfolioName.isEmpty()) {
            presenter.presentFail("Please choose a portfolio.");
            return;}
        if (symbol == null || symbol.isEmpty()) {
            presenter.presentFail("Please choose an asset.");
            return;}
        if (qty <= 0) {
            presenter.presentFail("Quantity must be positive.");
            return;}
        if (price <= 0) {
            presenter.presentFail("Price not loaded.");
            return;}

        List<SubAccount> accounts = subAccountDAO.getSubAccountsOf(username);
        SubAccount target = null;
        for (SubAccount sa : accounts) {
            if (sa.getName().equalsIgnoreCase(portfolioName)) {
                target = sa;
                break;}
        }

        if (target == null) {
            presenter.presentFail("Portfolio not found.");
            return;}

        BigDecimal cost = BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(qty));

        if (target.getBalanceUSD().compareTo(cost) < 0) {
            presenter.presentFail("Insufficient funds.");
            return;}

        BigDecimal newBal = target.getBalanceUSD().subtract(cost);
        target.setBalanceUSD(newBal);

        Stock stock = new Stock(symbol, qty, symbol);
        target.addOrIncreaseAsset(stock);

        subAccountDAO.save(username, target);

        subAccountDAO.save(username, target);

        presenter.presentSuccess(
                new BuyAssetOutputData(
                        "Purchased " + qty + " of " + symbol +
                                " for $" + cost + " in " + portfolioName + ".",
                        username
                )
        );
    }
}
