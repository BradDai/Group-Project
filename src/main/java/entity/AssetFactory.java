package entity;

public class AssetFactory {
    public Asset getAsset(String type, String name, double quantity, String Symbol) {
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
