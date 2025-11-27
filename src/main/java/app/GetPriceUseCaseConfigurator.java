package app;

import dataaccess.TwelveDataPriceGateway;
import interfaceadapter.buyasset.GetPriceController;
import interfaceadapter.buyasset.GetPricePresenter;
import usecase.get_price.GetPriceInputBoundary;
import usecase.get_price.GetPriceInteractor;
import usecase.get_price.PriceGateway;

/**
 * Wires the get-price use case used by the buy-asset view.
 */
public class GetPriceUseCaseConfigurator {

    private final ViewConfigurator views;
    private final String twelveDataApiKey;

    public GetPriceUseCaseConfigurator(
            final ViewConfigurator views,
            final String twelveDataApiKey
    ) {
        this.views = views;
        this.twelveDataApiKey = twelveDataApiKey;
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        GetPricePresenter presenter =
                new GetPricePresenter(views.getAssetViews().getBuyAssetViewModel());

        PriceGateway gateway = new TwelveDataPriceGateway(twelveDataApiKey);
        GetPriceInputBoundary interactor =
                new GetPriceInteractor(gateway, presenter);

        GetPriceController controller = new GetPriceController(interactor);
        views.getAssetViews().getBuyAssetView().setGetPriceController(controller);
    }
}
