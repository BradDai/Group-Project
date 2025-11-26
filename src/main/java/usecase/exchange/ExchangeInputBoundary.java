package usecase.exchange;

public interface ExchangeInputBoundary {
    void fetchExchangeRate(ExchangeInputData inputData);

    void convert(ExchangeConversionInputData inputData);
}
