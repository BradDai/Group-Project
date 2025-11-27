package usecase.get_price;

import java.io.IOException;

public class GetPriceInteractor implements GetPriceInputBoundary {

    private final PriceGateway gateway;
    private final GetPriceOutputBoundary presenter;

    public GetPriceInteractor(final PriceGateway gateway, final GetPriceOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(final GetPriceInputData inputData) {
        try {
            final double price = gateway.getPrice(inputData.symbol());
            presenter.presentPrice(new GetPriceOutputData(price));
        }
        catch (final IOException e) {
            presenter.presentError(e.getMessage());
        }
    }
}
