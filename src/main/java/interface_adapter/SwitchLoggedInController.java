package interface_adapter;

import use_case.switch_loggedin.SwitchLoggedInInputBoundary;

public class SwitchLoggedInController {

    private final SwitchLoggedInInputBoundary switchLoggedInUseCaseInteractor;

    public SwitchLoggedInController(SwitchLoggedInInputBoundary switchLoggedInUseCaseInteractor) {
        this.switchLoggedInUseCaseInteractor = switchLoggedInUseCaseInteractor;
    }

    public void switchToLoggedInView() {

        switchLoggedInUseCaseInteractor.switchToLoggedInView();
    }
}
