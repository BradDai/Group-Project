package app;

import dataaccess.FileSubAccountDataAccessJSON;
import interfaceadapter.ViewManagerModel;

/**
 * Aggregates navigation-related configurators.
 */
public class NavigationUseCaseConfigurator {

    private final FeatureSwitchUseCaseConfigurator featureSwitchConfigurator;
    private final SwitchLoggedInFromViewsConfigurator switchLoggedInConfigurator;

    public NavigationUseCaseConfigurator(
            final ViewManagerModel viewManagerModel,
            final FileSubAccountDataAccessJSON subAccountDataAccess,
            final ViewConfigurator views
    ) {
        this.featureSwitchConfigurator =
                new FeatureSwitchUseCaseConfigurator(
                        new SwitchExchangeUseCaseConfigurator(viewManagerModel, views),
                        new SwitchTransferUseCaseConfigurator(viewManagerModel, subAccountDataAccess, views),
                        new SwitchHistoryUseCaseConfigurator(viewManagerModel, views),
                        new SwitchBuyAssetUseCaseConfigurator(viewManagerModel, views),
                        new SwitchSellAssetUseCaseConfigurator(viewManagerModel, subAccountDataAccess, views)
                );

        this.switchLoggedInConfigurator =
                new SwitchLoggedInFromViewsConfigurator(
                        viewManagerModel,
                        subAccountDataAccess,
                        views
                );
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        featureSwitchConfigurator.wireUseCases();
        switchLoggedInConfigurator.wireUseCases();
    }
}
