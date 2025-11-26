package interface_adapter.sell_asset;

import java.util.Map;

public class SellAssetState {

    private double currentPrice;
    private String priceError;

    // for switch view
    private String username;
    private String[] portfolios;
    private Map<String, String[]> portfolioStocks;

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

    public void setPortfolioStocks(final Map<String, String[]> portfolioStocks) {
        this.portfolioStocks = portfolioStocks;
    }

    /**
     * Helper to get stocks for a given portfolio.
     *
     * @param portfolioName the name of portfolio
     * @return              the stock of given portfolio
     */
    public String[] getStocksOfPortfolio(final String portfolioName) {
        String[] result = null;
        if (portfolioStocks != null) {
            result = portfolioStocks.getOrDefault(portfolioName, new String[0]);
        }
        return result;
    }

}
