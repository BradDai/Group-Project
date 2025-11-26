package usecase.exchange;

public interface ExchangeOutputBoundary {
    void presentSuccess(ExchangeOutputData outputData);

    void presentFailure(String errorMessage);

    void presentConversionFailure(String errorMessage);

    void presentConversionSuccess(ExchangeConversionOutputData outputData);
}
