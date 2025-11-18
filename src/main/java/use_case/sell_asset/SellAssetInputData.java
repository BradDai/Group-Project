package use_case.sell_asset;

public class SellAssetInputData {
    private final String portfolioName;
    private final String assetName;
    private final Double quantityToSell;

    public SellAssetInputData(String portfolioName, String assetName, Double quantityToSell) {
        this.portfolioName = portfolioName;
        this.assetName = assetName;
        this.quantityToSell = quantityToSell;
    }

    public String getportfolioName() {return portfolioName;}
    public String getAssetName() {return assetName;}
    public Double getQuantityToSell() {return quantityToSell;}
}
