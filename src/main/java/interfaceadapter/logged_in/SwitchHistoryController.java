package interfaceadapter.logged_in;

import usecase.switch_history.SwitchHistoryInputBoundary;

public class SwitchHistoryController {

    private final SwitchHistoryInputBoundary switchHistoryUseCaseInteractor;

    public SwitchHistoryController(final SwitchHistoryInputBoundary switchHistoryUseCaseInteractor) {
        this.switchHistoryUseCaseInteractor = switchHistoryUseCaseInteractor;
    }

    /**
     * I.
     */
    public void switchToHistoryView() {

        switchHistoryUseCaseInteractor.switchToHistoryView();
    }
}
