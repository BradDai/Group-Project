package interface_adapter.logged_in;

import use_case.switch_history.SwitchHistoryInputBoundary;

public class SwitchHistoryController {

    private final SwitchHistoryInputBoundary switchHistoryUseCaseInteractor;

    public SwitchHistoryController(SwitchHistoryInputBoundary switchHistoryUseCaseInteractor) {
        this.switchHistoryUseCaseInteractor = switchHistoryUseCaseInteractor;
    }

    public void switchToHistoryView() {

        switchHistoryUseCaseInteractor.switchToHistoryView();
    }
}
