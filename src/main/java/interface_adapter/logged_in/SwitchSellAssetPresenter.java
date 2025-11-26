package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.sell_asset.SellAssetState;
import interface_adapter.sell_asset.SellAssetViewModel;
import use_case.switch_sellasset.SwitchSellAssetOutputBoundary;
import java.util.Map;

public class SwitchSellAssetPresenter implements SwitchSellAssetOutputBoundary {

    private final SellAssetViewModel sellAssetViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchSellAssetPresenter(final SellAssetViewModel sellAssetViewModel, final ViewManagerModel viewManagerModel) {
        this.sellAssetViewModel = sellAssetViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Switch to sell asset view.
     * @param username        the username
     * @param portfolios      portfolios
     * @param portfolioStocks stocks in each portfolio
     */
    public void switchToSellAssetView(final String username, final String[] portfolios, final Map<String, String[]> portfolioStocks) {
        final SellAssetState state = sellAssetViewModel.getState();
        state.setUsername(username);
        state.setPortfolios(portfolios);
        state.setPortfolioStocks(portfolioStocks);
        state.setErrorMessage("");

        sellAssetViewModel.setState(state);
        sellAssetViewModel.firePropertyChanged();

        viewManagerModel.setState(sellAssetViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
