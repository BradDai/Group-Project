package usecase.exchange;

public interface ExchangeInputBoundary {
    /**
     * Y.
     * @param inputData .
     */
    void fetchExchangeRate(ExchangeInputData inputData);

    /**
     * Y.
     * @param inputData .
     */
    void convert(ExchangeConversionInputData inputData);
}
