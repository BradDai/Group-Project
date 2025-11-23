package use_case.exchange;

public class ExchangeConversionInputData {

    private final String username;
    private final String accountName;
    private final String from;
    private final String to;
    private final double amount;

    public ExchangeConversionInputData(String username,
                                       String accountName,
                                       String from,
                                       String to,
                                       double amount) {
        this.username = username;
        this.accountName = accountName;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getUsername() { return username; }
    public String getAccountName() { return accountName; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getAmount() { return amount; }
}
