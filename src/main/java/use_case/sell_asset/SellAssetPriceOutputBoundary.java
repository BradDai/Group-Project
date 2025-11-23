package use_case.sell_asset;

public interface SellAssetPriceOutputBoundary {
    void preparePriceSuccessView(SellAssetPriceOutputData sellAssetPriceOutputData);
    void preparePriceFailureView(String errorMessage);
}
