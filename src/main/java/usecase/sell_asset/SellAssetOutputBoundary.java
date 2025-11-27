package usecase.sell_asset;

public interface SellAssetOutputBoundary {
    /**
     * Prepare the success view.
     *
     * @param sellAssetOutputData output data from interactor
     */
    void prepareSuccessView(SellAssetOutputData sellAssetOutputData);

    /**
     * Prepare the failure view.
     *
     * @param errorMessage error message from interactor
     */
    void prepareFailureView(String errorMessage);
}
