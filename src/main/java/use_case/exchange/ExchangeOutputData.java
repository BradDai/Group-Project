package use_case.exchange;

public class ExchangeOutputData {

    private final String from;
    private final String to;
    private final double rate;

    public ExchangeOutputData(String from, String to, double rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getRate() { return rate; }
}
