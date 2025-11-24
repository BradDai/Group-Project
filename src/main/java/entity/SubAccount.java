package entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SubAccount {
    private final String name;
    private BigDecimal balanceUSD;
    private final boolean undeletable;
    private final Map<String, BigDecimal> currencies = new HashMap<>();
    private final List<Asset> assets = new ArrayList<>();

    public SubAccount(final String name, final BigDecimal balanceUSD, final boolean undeletable) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        if (balanceUSD == null || balanceUSD.signum() < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }
        this.name = name.trim();
        this.balanceUSD = balanceUSD;
        this.undeletable = undeletable;
        currencies.put("USD", balanceUSD);
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
        return Collections.unmodifiableList(assets);
    }

    public void setBalanceUSD(final BigDecimal newBalance) {
        if (newBalance == null || newBalance.signum() < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }
        this.balanceUSD = newBalance;
        currencies.put("USD", newBalance);
    }

    public Map<String, BigDecimal> getCurrencies() {
        return Collections.unmodifiableMap(currencies);
    }

    public BigDecimal getBalanceOf(final String currencyCode) {
        return currencies.getOrDefault(currencyCode, BigDecimal.ZERO);
    }

    public void setBalanceOf(final String currencyCode, final BigDecimal amount) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new IllegalArgumentException("currencyCode required");
        }
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }
        currencies.put(currencyCode.toUpperCase(), amount);
        if ("USD".equalsIgnoreCase(currencyCode)) {
            this.balanceUSD = amount;
        }
    }

    public void addOrIncreaseAsset(final Asset newAsset) {
        for (final Asset asset : assets) {
            if (asset.getClass().equals(newAsset.getClass())
                && asset.getType().equalsIgnoreCase(newAsset.getType())) {

                final double newQuantity = asset.getQuantity() + newAsset.getQuantity();
                asset.setQuantity(newQuantity);
                return;
            }
        }
        assets.add(newAsset);
    }

    public void removeAsset(final Asset asset) {
        assets.remove(asset);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubAccount)) {
            return false;
        }
        final SubAccount that = (SubAccount) o;
        return name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return "SubAccount{name='" + name + "', currencies=" + currencies +
            ", undeletable=" + undeletable + "}";
    }
}
