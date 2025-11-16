package use_case.sell_asset;

public class SellAssetInputData {
    private final int portfolioId;
    private final String assetName;
    private final Double quantityToSell;

    public SellAssetInputData(int portfolioId, String assetName, Double quantityToSell) {
        this.portfolioId = portfolioId;
        this.assetName = assetName;
        this.quantityToSell = quantityToSell;
    }

    public int getPortfolioId() {return portfolioId;}
    public String getAssetName() {return assetName;}
    public Double getQuantityToSell() {return quantityToSell;}
}
