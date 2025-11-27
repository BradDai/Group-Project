package usecase.sell_asset;

public interface SellAssetInputBoundary {
    /**
     * Execute the sell asset interactor.
     *
     * @param sellAssetInputData sell asset input data from view
     */
    void execute(SellAssetInputData sellAssetInputData);

    /**
     * Fetch the price for the given stock.
     *
     * @param stockName the stock name
     */
    void fetchPrice(String stockName);
}
