package interfaceadapter.logged_in;

import java.util.Map;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.sell_asset.SellAssetState;
import interfaceadapter.sell_asset.SellAssetViewModel;
import usecase.switch_sellasset.SwitchSellAssetOutputBoundary;

public class SwitchSellAssetPresenter implements SwitchSellAssetOutputBoundary {

    private final SellAssetViewModel sellAssetViewModel;
    private final ViewManagerModel viewManagerModel;

    public SwitchSellAssetPresenter(final SellAssetViewModel sellAssetViewModel,
                                    final ViewManagerModel viewManagerModel) {
        this.sellAssetViewModel = sellAssetViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Switch to sell asset view.
     * @param username        the username
     * @param portfolios      portfolios
     * @param portfolioStocks stocks in each portfolio
     */
    public void switchToSellAssetView(final String username, final String[] portfolios,
                                      final Map<String, String[]> portfolioStocks) {
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
