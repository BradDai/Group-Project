package app;

/**
 * Aggregates feature-switch use case configurators.
 */
public class FeatureSwitchUseCaseConfigurator {

    private final SwitchExchangeUseCaseConfigurator exchangeConfigurator;
    private final SwitchTransferUseCaseConfigurator transferConfigurator;
    private final SwitchHistoryUseCaseConfigurator historyConfigurator;
    private final SwitchBuyAssetUseCaseConfigurator buyAssetConfigurator;
    private final SwitchSellAssetUseCaseConfigurator sellAssetConfigurator;

    public FeatureSwitchUseCaseConfigurator(
            final SwitchExchangeUseCaseConfigurator exchangeConfigurator,
            final SwitchTransferUseCaseConfigurator transferConfigurator,
            final SwitchHistoryUseCaseConfigurator historyConfigurator,
            final SwitchBuyAssetUseCaseConfigurator buyAssetConfigurator,
            final SwitchSellAssetUseCaseConfigurator sellAssetConfigurator
    ) {
        this.exchangeConfigurator = exchangeConfigurator;
        this.transferConfigurator = transferConfigurator;
        this.historyConfigurator = historyConfigurator;
        this.buyAssetConfigurator = buyAssetConfigurator;
        this.sellAssetConfigurator = sellAssetConfigurator;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        exchangeConfigurator.wireUseCase();
        transferConfigurator.wireUseCase();
        historyConfigurator.wireUseCase();
        buyAssetConfigurator.wireUseCase();
        sellAssetConfigurator.wireUseCase();
    }
}
