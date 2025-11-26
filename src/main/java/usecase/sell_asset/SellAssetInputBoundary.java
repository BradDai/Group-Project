package usecase.sell_asset;

public interface SellAssetInputBoundary {
    void execute(SellAssetInputData sellAssetInputData);

    void fetchPrice(String stockName);
}
