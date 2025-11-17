package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.history.HistoryViewModel;
import use_case.switch_history.SwitchHistoryOutputBoundary;

public class SwitchHistoryPresenter implements SwitchHistoryOutputBoundary {

    private final HistoryViewModel historyViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchHistoryPresenter(HistoryViewModel historyViewModel, ViewManagerModel viewManagerModel) {

        this.historyViewModel = historyViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    public void switchToHistoryView() {

        viewManagerModel.setState(historyViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
