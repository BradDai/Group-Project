package entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class SubAccount {
    private final String name;
    private BigDecimal balanceUSD;
    private final boolean undeletable;
    private final List<Asset> assets = new ArrayList<>();
    public SubAccount(String name, BigDecimal balanceUSD, boolean undeletable) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        if (balanceUSD == null || balanceUSD.signum() < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }
        this.name = name.trim();
        this.balanceUSD = balanceUSD;
        this.undeletable = undeletable;
    }
    public String getName() {

        return name;
    }
    public BigDecimal getBalanceUSD() {

        return balanceUSD;
    }
    public boolean isUndeletable() {

        return undeletable;
    }
    public List<Asset> getAssets() {
        return Collections.unmodifiableList(assets);}
    public void setBalanceUSD(BigDecimal newBalance) {
        if (newBalance == null || newBalance.signum() < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }
        this.balanceUSD = newBalance;
    }
    public void addOrIncreaseAsset(Asset newAsset) {
        for (Asset asset : assets) {
            if (asset.getClass().equals(newAsset.getClass())
                    && asset.getType().equalsIgnoreCase(newAsset.getType())) {
                double newQuantity = asset.getQuantity() + newAsset.getQuantity();
            }
        }
        assets.add(newAsset);
    }
    public void removeAsset(Asset asset) {
        assets.remove(asset);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubAccount)) return false;
        SubAccount that = (SubAccount) o;
        return name.equalsIgnoreCase(that.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
    @Override
    public String toString() {
        return "SubAccount{name='" + name + "', balanceUSD=" + balanceUSD +
                ", undeletable=" + undeletable + "}";
    }
}