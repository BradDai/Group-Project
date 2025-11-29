package usecase.exchange;

public interface ExchangeOutputBoundary {
    /**
     * Y.
     * @param outputData .
     */
    void presentSuccess(ExchangeOutputData outputData);

    /**
     * Y.
     * @param errorMessage .
     */
    void presentFailure(String errorMessage);

    /**
     * Y.
     * @param errorMessage .
     */
    void presentConversionFailure(String errorMessage);

    /**
     * Y.
     * @param outputData .
     */
    void presentConversionSuccess(ExchangeConversionOutputData outputData);
}
