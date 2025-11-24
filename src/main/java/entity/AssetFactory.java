package entity;

public class AssetFactory {
    public Asset getAsset(final String type, final String name, final double quantity, final String Symbol) {
        switch (type) {
            case "Stock":
                return new Stock(name, quantity, Symbol);
            case "Currency":
                return new Currency(name, quantity, Symbol);
            default:
                throw new IllegalArgumentException("Invalid type " + type);
        }
    }
}
