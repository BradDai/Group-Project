package use_case.exchange;

public interface ExchangeOutputBoundary {
    void presentSuccess(ExchangeOutputData outputData);
    void presentFailure(String errorMessage);
}
