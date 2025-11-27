package app;

import dataaccess.FileSubAccountDataAccessJSON;
import dataaccess.TransactionDataAccessInterface;
import interfaceadapter.ViewManagerModel;

/**
 * Aggregates asset-related use case configurators.
 */
public class AssetUseCaseConfigurator {

    private final BuyAssetUseCaseConfigurator buyConfigurator;
    private final SellAssetUseCaseConfigurator sellConfigurator;
    private final GetPriceUseCaseConfigurator priceConfigurator;

    public AssetUseCaseConfigurator(
            final ViewManagerModel viewManagerModel,
            final FileSubAccountDataAccessJSON subAccountDataAccess,
            final TransactionDataAccessInterface transactionDataAccessObject,
            final ViewConfigurator views,
            final String twelveDataApiKey
    ) {
        this.buyConfigurator =
                new BuyAssetUseCaseConfigurator(
                        subAccountDataAccess,
                        transactionDataAccessObject,
                        views
                );

        this.sellConfigurator =
                new SellAssetUseCaseConfigurator(
                        subAccountDataAccess,
                        transactionDataAccessObject,
                        views
                );

        this.priceConfigurator =
                new GetPriceUseCaseConfigurator(
                        views,
                        twelveDataApiKey
                );
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        buyConfigurator.wireUseCases();
        sellConfigurator.wireUseCases();
        priceConfigurator.wireUseCases();
    }
}
