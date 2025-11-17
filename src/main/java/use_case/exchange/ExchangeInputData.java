package use_case.exchange;

public class ExchangeInputData {

    private final String from;
    private final String to;

    public ExchangeInputData(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
