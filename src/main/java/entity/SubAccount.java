package entity;

import java.math.BigDecimal;
import java.util.Objects;


public class SubAccount {
    private final String name;
    private BigDecimal balanceUSD;
    private final boolean undeletable;
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
    public void setBalanceUSD(BigDecimal newBalance) {
        if (newBalance == null || newBalance.signum() < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }
        this.balanceUSD = newBalance;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubAccount)) return false;
        SubAccount that = (SubAccount) o;
        // 这里默认：在同一个用户内部，名字唯一，所以只按 name 比较
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