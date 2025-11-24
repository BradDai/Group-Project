package use_case.buyasset;

public class BuyAssetInputData {
    private final String username;
    private final String portfolioName;
    private final String symbol;
    private final int quantity;
    private final double price; // price per unit

    public BuyAssetInputData(String username, String portfolioName,
                             String symbol, int quantity, double price) {
        this.username = username;
        this.portfolioName = portfolioName;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    public String getUsername() {return username;}
    public String getPortfolioName() {return portfolioName;}
    public String getSymbol() {return symbol;}
    public int getQuantity() {return quantity;}
    public double getPrice() {return price;}
}