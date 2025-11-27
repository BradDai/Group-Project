package usecase.sell_asset;

public interface SellAssetPriceOutputBoundary {
    void preparePriceSuccessView(SellAssetPriceOutputData sellAssetPriceOutputData);

    void preparePriceFailureView(String errorMessage);
}
