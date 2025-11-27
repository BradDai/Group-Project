package usecase.buyasset;

public interface BuyAssetOutputBoundary {
    void presentSuccess(BuyAssetOutputData outputData);

    void presentFail(String errorMessage);
}
