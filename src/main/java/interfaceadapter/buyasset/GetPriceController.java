package interfaceadapter.buyasset;

import usecase.get_price.GetPriceInputBoundary;
import usecase.get_price.GetPriceInputData;

public class GetPriceController {
    private final GetPriceInputBoundary interactor;

    public GetPriceController(final GetPriceInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * I.
     * @param symbol .
     */
    public void execute(final String symbol) {
        interactor.execute(new GetPriceInputData(symbol));
    }
}
