package use_case.switch_loggedin;

public class SwitchLoggedInInteractor implements SwitchLoggedInInputBoundary {

    private final SwitchLoggedInOutputBoundary switchLoggedInPresenter;

    public SwitchLoggedInInteractor(final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary) {
        this.switchLoggedInPresenter = switchLoggedInOutputBoundary;
    }

    public void switchToLoggedInView() {
        switchLoggedInPresenter.switchToLoggedInView();
    }
}
