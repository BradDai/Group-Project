package use_case.sell_asset;

public interface SellAssetOutputBoundary {
    void prepareSuccessView(SellAssetOutputData sellAssetOutputData);
    void prepareFailureView(String errorMessage);
}
