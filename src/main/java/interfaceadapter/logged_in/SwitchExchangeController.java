package interfaceadapter.logged_in;

import usecase.switch_exchange.SwitchExchangeInputBoundary;

public class SwitchExchangeController {

    private final SwitchExchangeInputBoundary switchExchangeUseCaseInteractor;

    public SwitchExchangeController(final SwitchExchangeInputBoundary switchExchangeUseCaseInteractor) {
        this.switchExchangeUseCaseInteractor = switchExchangeUseCaseInteractor;
    }

    /**
     * I.
     * @param username .
     */
    public void switchToExchangeView(final String username) {
        switchExchangeUseCaseInteractor.switchToExchangeView(username);
    }

}
