package usecase.buyasset;

public interface BuyAssetOutputBoundary {
    /**
     * Present success.
     *
     * @param outputData .
     */
    void presentSuccess(BuyAssetOutputData outputData);

    /**
     * Error Message.
     *
     * @param errorMessage .
     */
    void presentFail(String errorMessage);
}
