package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

import interfaceadapter.exchange.ExchangeViewModel;
import interfaceadapter.transfer.TransferViewModel;
import view.ExchangeView;
import view.TransferView;

/**
 * Configures the exchange and transfer views and view models.
 */
public class MoneyViewConfigurator {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private ExchangeViewModel exchangeViewModel;
    private ExchangeView exchangeView;

    private TransferViewModel transferViewModel;
    private TransferView transferView;

    public MoneyViewConfigurator(final JPanel cardPanel, final CardLayout cardLayout) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
    }
    /**
     * Creates views.
     */

    public void createViews() {
        exchangeViewModel = new ExchangeViewModel();
        exchangeView = new ExchangeView(exchangeViewModel);
        cardPanel.add(exchangeView, exchangeView.getViewName());

        transferViewModel = new TransferViewModel();
        transferView = new TransferView(transferViewModel);
        cardPanel.add(transferView, transferView.getViewName());
    }

    public ExchangeViewModel getExchangeViewModel() {
        return exchangeViewModel;
    }

    public ExchangeView getExchangeView() {
        return exchangeView;
    }

    public TransferViewModel getTransferViewModel() {
        return transferViewModel;
    }

    public TransferView getTransferView() {
        return transferView;
    }
}
