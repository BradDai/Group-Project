package interfaceadapter.exchange;

public class ExchangeState {

    private String username = "";
    private String conversionMessage = "";
    private String errorMessage = "";
    private String amountField = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getAmountField() {
        return amountField;
    }

    public void setAmountField(final String msg) {
        this.amountField = msg;
    }

    public String getConversionMessage() {
        return conversionMessage;
    }

    public void setConversionMessage(final String msg) {
        this.conversionMessage = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String msg) {
        this.errorMessage = msg;
    }
}
