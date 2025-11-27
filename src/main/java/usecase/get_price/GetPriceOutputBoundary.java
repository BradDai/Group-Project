package usecase.get_price;

public interface GetPriceOutputBoundary {
    void presentPrice(GetPriceOutputData data);

    void presentError(String message);
}
