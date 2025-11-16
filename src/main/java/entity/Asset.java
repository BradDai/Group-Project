package entity;


public class Asset {
    private String type;
    private double quantity;

    public Asset(String type, double quantity) {
        this.type = type;
        this.quantity = quantity;
    }

    /**
     * Creates a new asset category if none was presented. with the given non-empty name and non-empty password.
     * @param type the name of the asset
     * @param quantity the quantity of asset gained
     * @throws IllegalArgumentException if the type of quantity are empty
     */

    public Asset addNewAsset(String type, Double quantity, String price) {
        if ("".equals(type)) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        if ("".equals(quantity)) {
            throw new IllegalArgumentException("Quantity cannot be empty");
        }
        this.quantity = quantity;
        this.type = type;
        return this;
    }

    public String getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public Asset addAsset(Asset[] xxx, String type, int quantity) {
        for (Asset asset : xxx) {
            if (asset.type.equals(type)) {
                asset.quantity += quantity;
            }
        }
        return this;
    }

}

