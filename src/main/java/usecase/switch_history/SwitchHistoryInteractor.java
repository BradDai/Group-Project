package usecase.switch_history;

public class SwitchHistoryInteractor implements SwitchHistoryInputBoundary {

    private final SwitchHistoryOutputBoundary switchHistoryPresenter;

    public SwitchHistoryInteractor(final SwitchHistoryOutputBoundary switchHistoryOutputBoundary) {
        this.switchHistoryPresenter = switchHistoryOutputBoundary;
    }

    /**
     * I.
     */
    public void switchToHistoryView() {
        switchHistoryPresenter.switchToHistoryView();
    }
}
