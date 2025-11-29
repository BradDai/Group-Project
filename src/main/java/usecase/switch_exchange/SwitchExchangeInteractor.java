package usecase.switch_exchange;

public class SwitchExchangeInteractor implements SwitchExchangeInputBoundary {

    private final SwitchExchangeOutputBoundary switchExchangePresenter;

    public SwitchExchangeInteractor(final SwitchExchangeOutputBoundary switchExchangeOutputBoundary) {
        this.switchExchangePresenter = switchExchangeOutputBoundary;
    }

    /**
     * I.
     * @param username .
     */
    public void switchToExchangeView(final String username) {
        switchExchangePresenter.switchToExchangeView(username);
    }
}
