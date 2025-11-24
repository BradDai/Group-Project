package interface_adapter.sell_asset;

public class SellAssetState {

    private double currentPrice;
    private String priceError;

    // for switch view
    private String username;
    private String[] portfolios;

    // output
    private String message;
    private String errorMessage;

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(final double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getPriceError() {
        return priceError;
    }

    public void setPriceError(final String priceError) {
        this.priceError = priceError;
    }

    // methods for switch view
    public void setUsername(final String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPortfolios(final String[] portfolios) {
        this.portfolios = portfolios;
    }

    public String[] getPortfolios() {
        return portfolios;
    }

    // for output
    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
