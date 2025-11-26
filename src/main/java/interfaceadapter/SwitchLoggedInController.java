package interfaceadapter;

import usecase.switch_loggedin.SwitchLoggedInInputBoundary;

public class SwitchLoggedInController {

    private final SwitchLoggedInInputBoundary switchLoggedInUseCaseInteractor;

    public SwitchLoggedInController(final SwitchLoggedInInputBoundary switchLoggedInUseCaseInteractor) {
        this.switchLoggedInUseCaseInteractor = switchLoggedInUseCaseInteractor;
    }

    public void switchToLoggedInView() {

        switchLoggedInUseCaseInteractor.switchToLoggedInView();
    }
}
