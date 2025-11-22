package use_case.switch_exchange;

public class SwitchExchangeInteractor implements SwitchExchangeInputBoundary {

    private final SwitchExchangeOutputBoundary switchExchangePresenter;

    public SwitchExchangeInteractor(SwitchExchangeOutputBoundary switchExchangeOutputBoundary) {
        this.switchExchangePresenter = switchExchangeOutputBoundary;
    }
    public void switchToExchangeView(String username) {
        switchExchangePresenter.switchToExchangeView(username);
    }
}
