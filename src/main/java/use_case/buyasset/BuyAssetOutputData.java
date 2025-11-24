package use_case.buyasset;

public class BuyAssetOutputData {
    private final String message;
    private final String username;

    public BuyAssetOutputData(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
