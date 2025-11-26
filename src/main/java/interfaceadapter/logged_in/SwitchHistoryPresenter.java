package interfaceadapter.logged_in;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.history.HistoryViewModel;
import usecase.switch_history.SwitchHistoryOutputBoundary;

public class SwitchHistoryPresenter implements SwitchHistoryOutputBoundary {

    private final HistoryViewModel historyViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchHistoryPresenter(final HistoryViewModel historyViewModel, final ViewManagerModel viewManagerModel) {

        this.historyViewModel = historyViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    public void switchToHistoryView() {

        viewManagerModel.setState(historyViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
