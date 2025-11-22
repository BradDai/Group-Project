package interface_adapter.logged_in;

import use_case.switch_exchange.SwitchExchangeInputBoundary;

public class SwitchExchangeController {

    private final SwitchExchangeInputBoundary switchExchangeUseCaseInteractor;

    public SwitchExchangeController(SwitchExchangeInputBoundary switchExchangeUseCaseInteractor) {
        this.switchExchangeUseCaseInteractor = switchExchangeUseCaseInteractor;
    }

    public void switchToExchangeView(String username) {
        switchExchangeUseCaseInteractor.switchToExchangeView(username);
    }

}
