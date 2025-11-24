package use_case.get_price;

import java.io.IOException;

public class GetPriceInteractor implements GetPriceInputBoundary {

    private final PriceGateway gateway;
    private final GetPriceOutputBoundary presenter;

    public GetPriceInteractor(PriceGateway gateway, GetPriceOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(GetPriceInputData inputData) {
        try {
            double price = gateway.getPrice(inputData.getSymbol());
            presenter.presentPrice(new GetPriceOutputData(price));
        } catch (IOException e) {
            presenter.presentError(e.getMessage());
        }
    }
}