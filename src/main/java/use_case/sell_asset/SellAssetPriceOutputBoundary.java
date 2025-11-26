package use_case.sell_asset;

public interface SellAssetPriceOutputBoundary {
    /**
     * Prepare success view for fetch price.
     *
     * @param sellAssetPriceOutputData fetch price output data from interactor
     */
    void preparePriceSuccessView(SellAssetPriceOutputData sellAssetPriceOutputData);

    /**
     * Prepare failure view for fetch price.
     *
     * @param errorMessage fetch price error message from interactor
     */
    void preparePriceFailureView(String errorMessage);
}
