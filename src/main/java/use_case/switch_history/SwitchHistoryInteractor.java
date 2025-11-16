package use_case.switch_history;

public class SwitchHistoryInteractor implements SwitchHistoryInputBoundary {

    private final SwitchHistoryOutputBoundary switchHistoryPresenter;

    public SwitchHistoryInteractor(SwitchHistoryOutputBoundary switchHistoryOutputBoundary) {
        this.switchHistoryPresenter = switchHistoryOutputBoundary;
    }

    public void switchToHistoryView() { switchHistoryPresenter.switchToHistoryView(); }
}
