package interface_adapter.exchange;

public class ExchangeState {

    private String username = "";
    private String amountField = "";
    private String conversionMessage = "";
    private String errorMessage = "";
    private String balanceMessage = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getAmountField() {
        return amountField;
    }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String msg) { this.errorMessage = msg; }

    public String getBalanceMessage() { return balanceMessage; }
    public void setBalanceMessage(String msg) { this.balanceMessage = msg; }
}
