package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

import interfaceadapter.history.HistoryViewModel;
import view.HistoryView;

/**
 * Configures the history view and view model.
 */
public class HistoryViewConfigurator {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private HistoryViewModel historyViewModel;
    private HistoryView historyView;

    public HistoryViewConfigurator(final JPanel cardPanel, final CardLayout cardLayout) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
    }
    /**
     * Creates views.
     */

    public void createViews() {
        historyViewModel = new HistoryViewModel();
        historyView = new HistoryView(historyViewModel);
        cardPanel.add(historyView, historyView.getViewName());
    }

    public HistoryViewModel getHistoryViewModel() {
        return historyViewModel;
    }

    public HistoryView getHistoryView() {
        return historyView;
    }
}



