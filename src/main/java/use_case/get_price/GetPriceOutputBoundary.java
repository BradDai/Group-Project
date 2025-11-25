package use_case.get_price;

public interface GetPriceOutputBoundary {
    void presentPrice(GetPriceOutputData data);

    void presentError(String message);
}
