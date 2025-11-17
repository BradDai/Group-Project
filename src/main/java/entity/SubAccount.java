package entity;

import java.math.BigDecimal;
import java.util.Objects;

public class SubAccount {
    private final String name;
    private BigDecimal balanceCAD;
    private final boolean undeletable;
    public SubAccount(String name, BigDecimal balanceCAD, boolean undeletable) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        if (balanceCAD == null || balanceCAD.signum() < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }
        this.name = name.trim();
        this.balanceCAD = balanceCAD;
        this.undeletable = undeletable;
    }
    public String getName() {

        return name;
    }
    public BigDecimal getBalanceCAD() {

        return balanceCAD;
    }
    public boolean isUndeletable() {

        return undeletable;
    }
    public void setBalanceCAD(BigDecimal newBalance) {
        if (newBalance == null || newBalance.signum() < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }
        this.balanceCAD = newBalance;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SubAccount))
            return false;
        SubAccount that = (SubAccount) o;
        return name.equalsIgnoreCase(that.name);
    }
    @Override
    public int hashCode() {

        return Objects.hash(name.toLowerCase());
    }
    @Override
    public String toString() {
        return "SubAccount{name='" + name + "', balanceCAD=" + balanceCAD +
                ", undeletable=" + undeletable + "}";
    }
}
