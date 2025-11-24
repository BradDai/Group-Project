package interface_adapter.buyasset;

import use_case.get_price.GetPriceInputBoundary;
import use_case.get_price.GetPriceInputData;

public class GetPriceController {
    private final GetPriceInputBoundary interactor;

    public GetPriceController(GetPriceInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String symbol) {
        interactor.execute(new GetPriceInputData(symbol));
    }
}
