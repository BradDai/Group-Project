package interface_adapter.sell_asset;

public class SellAssetState {

    private double currentPrice;
    private String priceError;

    // for switch view
    private String username;
    private String[] portfolios;

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getPriceError() {
        return priceError;
    }

    public void setPriceError(String priceError) {
        this.priceError = priceError;
    }

    // methods for switch view
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPortfolios(String[] portfolios) {
        this.portfolios = portfolios;
    }

    public String[] getPortfolios() {
        return portfolios;
    }

    public void setErrorMessage(String errorMessage) {
        this.priceError = errorMessage;
    }
}