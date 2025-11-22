package interface_adapter.exchange;

public class ExchangeState {

    private String username = "";
    private String conversionMessage = "";
    private String errorMessage = "";
    private String amountField = "";

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAmountField() { return amountField; }
    public void setAmountField(String msg) { this.amountField = msg; }

    public String getConversionMessage() { return conversionMessage; }
    public void setConversionMessage(String msg) { this.conversionMessage = msg; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String msg) { this.errorMessage = msg; }
}
