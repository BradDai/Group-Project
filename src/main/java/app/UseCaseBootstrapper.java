package app;

import interfaceadapter.ViewManagerModel;

/**
 * Creates and wires all use case configurators.
 */
public class UseCaseBootstrapper {

    private final AuthUseCaseConfigurator authConfigurator;
    private final AssetUseCaseConfigurator assetConfigurator;
    private final MoneyUseCaseConfigurator moneyConfigurator;
    private final HistoryUseCaseConfigurator historyConfigurator;
    private final NavigationUseCaseConfigurator navigationConfigurator;
    private final SubAccountUseCaseConfigurator subAccountConfigurator;

    public UseCaseBootstrapper(
            final ViewManagerModel viewManagerModel,
            final InfrastructureConfig infra,
            final ViewConfigurator views
    ) {
        this.authConfigurator = new AuthUseCaseConfigurator(
                viewManagerModel,
                infra.getUserFactory(),
                infra.getUserDataAccessObject(),
                infra.getSubAccountDataAccess(),
                views
        );

        this.assetConfigurator = new AssetUseCaseConfigurator(
                viewManagerModel,
                infra.getSubAccountDataAccess(),
                infra.getTransactionDataAccessObject(),
                views,
                infra.getTwelveDataApiKey()
        );

        this.moneyConfigurator = new MoneyUseCaseConfigurator(
                viewManagerModel,
                infra.getSubAccountDataAccess(),
                infra.getTransactionDataAccessObject(),
                views
        );

        this.historyConfigurator = new HistoryUseCaseConfigurator(
                infra.getTransactionDataAccessObject(),
                views
        );

        this.navigationConfigurator = new NavigationUseCaseConfigurator(
                viewManagerModel,
                infra.getSubAccountDataAccess(),
                views
        );

        this.subAccountConfigurator = new SubAccountUseCaseConfigurator(
                infra.getSubAccountDataAccess(),
                views
        );
    }

    /**
     * Wires all use cases by delegating to individual configurators.
     */
    public void wireAllUseCases() {
        authConfigurator.wireUseCases();
        assetConfigurator.wireUseCases();
        moneyConfigurator.wireUseCases();
        historyConfigurator.wireUseCases();
        navigationConfigurator.wireUseCases();
        subAccountConfigurator.wireUseCases();
    }
}
