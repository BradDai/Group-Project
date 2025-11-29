package usecase.get_price;

public interface GetPriceOutputBoundary {
    /**
     * L.
     * @param data .
     */
    void presentPrice(GetPriceOutputData data);

    /**
     * L.
     * @param message .
     */
    void presentError(String message);
}
