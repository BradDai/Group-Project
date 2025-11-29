package entity;

public class Asset {
    private final String type;
    private double quantity;

    public Asset(final String type, final double quantity) {
        this.type = type;
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(final double quantity) {
        this.quantity = quantity;
    }

}

