package app;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jetbrains.annotations.NotNull;

import dataaccess.FileSubAccountDataAccessJSON;
import dataaccess.FileTransactionDataAccess;
import dataaccess.FileUserDataAccessObject;
import dataaccess.TransactionDataAccessInterface;
import dataaccess.TwelveDataPriceGateway;
import entity.UserFactory;
import interfaceadapter.SwitchLoggedInController;
import interfaceadapter.SwitchLoggedInPresenter;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.buyasset.BuyAssetController;
import interfaceadapter.buyasset.BuyAssetPresenter;
import interfaceadapter.buyasset.BuyAssetViewModel;
import interfaceadapter.buyasset.GetPriceController;
import interfaceadapter.buyasset.GetPricePresenter;
import interfaceadapter.exchange.ExchangeController;
import interfaceadapter.exchange.ExchangePresenter;
import interfaceadapter.exchange.ExchangeViewModel;
import interfaceadapter.history.HistoryViewModel;
import interfaceadapter.history.TransactionHistoryController;
import interfaceadapter.history.TransactionHistoryPresenter;
import interfaceadapter.logged_in.ChangePasswordController;
import interfaceadapter.logged_in.ChangePasswordPresenter;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.logged_in.SwitchBuyAssetController;
import interfaceadapter.logged_in.SwitchBuyAssetPresenter;
import interfaceadapter.logged_in.SwitchExchangeController;
import interfaceadapter.logged_in.SwitchExchangePresenter;
import interfaceadapter.logged_in.SwitchHistoryController;
import interfaceadapter.logged_in.SwitchHistoryPresenter;
import interfaceadapter.logged_in.SwitchSellAssetController;
import interfaceadapter.logged_in.SwitchSellAssetPresenter;
import interfaceadapter.logged_in.SwitchTransferController;
import interfaceadapter.logged_in.SwitchTransferPresenter;
import interfaceadapter.login.LoginController;
import interfaceadapter.login.LoginPresenter;
import interfaceadapter.login.LoginViewModel;
import interfaceadapter.logout.LogoutController;
import interfaceadapter.logout.LogoutPresenter;
import interfaceadapter.sell_asset.SellAssetController;
import interfaceadapter.sell_asset.SellAssetPresenter;
import interfaceadapter.sell_asset.SellAssetViewModel;
import interfaceadapter.signup.SignupController;
import interfaceadapter.signup.SignupPresenter;
import interfaceadapter.signup.SignupViewModel;
import interfaceadapter.subaccount.create.CreateSubAccountController;
import interfaceadapter.subaccount.create.CreateSubAccountPresenter;
import interfaceadapter.subaccount.delete.DeleteSubAccountController;
import interfaceadapter.subaccount.delete.DeleteSubAccountPresenter;
import interfaceadapter.transfer.TransferController;
import interfaceadapter.transfer.TransferPresenter;
import interfaceadapter.transfer.TransferViewModel;
import usecase.SubAccount.create.CreateSubAccountInputBoundary;
import usecase.SubAccount.create.CreateSubAccountInteractor;
import usecase.SubAccount.create.CreateSubAccountOutputBoundary;
import usecase.SubAccount.delete.DeleteSubAccountInputBoundary;
import usecase.SubAccount.delete.DeleteSubAccountInteractor;
import usecase.SubAccount.delete.DeleteSubAccountOutputBoundary;
import usecase.buyasset.BuyAssetInputBoundary;
import usecase.buyasset.BuyAssetInteractor;
import usecase.change_password.ChangePasswordInputBoundary;
import usecase.change_password.ChangePasswordInteractor;
import usecase.change_password.ChangePasswordOutputBoundary;
import usecase.exchange.ExchangeInputBoundary;
import usecase.exchange.ExchangeInteractor;
import usecase.exchange.ExchangeOutputBoundary;
import usecase.get_price.GetPriceInputBoundary;
import usecase.get_price.GetPriceInteractor;
import usecase.get_price.PriceGateway;
import usecase.login.LoginInputBoundary;
import usecase.login.LoginInteractor;
import usecase.login.LoginOutputBoundary;
import usecase.logout.LogoutInputBoundary;
import usecase.logout.LogoutInteractor;
import usecase.logout.LogoutOutputBoundary;
import usecase.sell_asset.SellAssetInputBoundary;
import usecase.sell_asset.SellAssetInteractor;
import usecase.signup.SignupInputBoundary;
import usecase.signup.SignupInteractor;
import usecase.signup.SignupOutputBoundary;
import usecase.switch_buyasset.SwitchBuyAssetInputBoundary;
import usecase.switch_buyasset.SwitchBuyAssetInteractor;
import usecase.switch_buyasset.SwitchBuyAssetOutputBoundary;
import usecase.switch_exchange.SwitchExchangeInputBoundary;
import usecase.switch_exchange.SwitchExchangeInteractor;
import usecase.switch_exchange.SwitchExchangeOutputBoundary;
import usecase.switch_history.SwitchHistoryInputBoundary;
import usecase.switch_history.SwitchHistoryInteractor;
import usecase.switch_history.SwitchHistoryOutputBoundary;
import usecase.switch_loggedin.SwitchLoggedInInputBoundary;
import usecase.switch_loggedin.SwitchLoggedInInteractor;
import usecase.switch_loggedin.SwitchLoggedInOutputBoundary;
import usecase.switch_sellasset.SwitchSellAssetInputBoundary;
import usecase.switch_sellasset.SwitchSellAssetInteractor;
import usecase.switch_sellasset.SwitchSellAssetOutputBoundary;
import usecase.switch_transfer.SwitchTransferInputBoundary;
import usecase.switch_transfer.SwitchTransferInteractor;
import usecase.switch_transfer.SwitchTransferOutputBoundary;
import usecase.transaction_history.TransactionHistoryInputBoundary;
import usecase.transaction_history.TransactionHistoryInteractor;
import usecase.transaction_history.TransactionHistoryOutputBoundary;
import usecase.transfer.TransferInputBoundary;
import usecase.transfer.TransferInteractor;
import usecase.transfer.TransferOutputBoundary;
import view.BuyAssetView;
import view.ExchangeView;
import view.HistoryView;
import view.LoggedInView;
import view.LoginView;
import view.SellAssetView;
import view.SignupView;
import view.TransferView;
import view.ViewManager;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);
    private final FileSubAccountDataAccessJSON subAccountDataAccess =
        new FileSubAccountDataAccessJSON("subaccounts.json");
    private static final String TWELVE_DATA_API_KEY = "ebcea301f0ad46579daa6b6dea349164";

    private final TransactionDataAccessInterface transactionDataAccessObject =
        new FileTransactionDataAccess("data/transactions");
    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private ExchangeViewModel exchangeViewModel;
    private ExchangeView exchangeView;
    private TransferViewModel transferViewModel;
    private TransferView transferView;
    private HistoryViewModel historyViewModel;
    private HistoryView historyView;
    private BuyAssetViewModel buyAssetViewModel;
    private BuyAssetView buyAssetView;
    private SellAssetViewModel sellAssetViewModel;
    private SellAssetView sellAssetView;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    public AppBuilder addExchangeView() {
        exchangeViewModel = new ExchangeViewModel();
        exchangeView = new ExchangeView(exchangeViewModel);
        cardPanel.add(exchangeView, exchangeView.getViewName());
        return this;
    }

    public AppBuilder addTransferView() {
        transferViewModel = new TransferViewModel();
        transferView = new TransferView(transferViewModel);
        cardPanel.add(transferView, transferView.getViewName());
        return this;
    }

    public AppBuilder addHistoryView() {
        historyViewModel = new HistoryViewModel();
        historyView = new HistoryView(historyViewModel);
        cardPanel.add(historyView, historyView.getViewName());
        return this;
    }

    public AppBuilder addTransactionHistoryUsecase() {
        final TransactionHistoryOutputBoundary presenter =
            new TransactionHistoryPresenter(historyViewModel);

        final TransactionHistoryInputBoundary interactor =
            new TransactionHistoryInteractor(transactionDataAccessObject,
                presenter,
                loggedInViewModel);

        final TransactionHistoryController controller =
            new TransactionHistoryController(interactor);

        historyView.setTransactionHistoryController(controller);
        return this;
    }


    public AppBuilder addBuyAssetView() {
        buyAssetViewModel = new BuyAssetViewModel();
        buyAssetView = new BuyAssetView(buyAssetViewModel);
        cardPanel.add(buyAssetView, buyAssetView.getViewName());
        buyAssetView.setLoggedInViewModel(loggedInViewModel);
        return this;
    }

    public AppBuilder addGetPriceUseCase() {
        final GetPricePresenter presenter = new GetPricePresenter(buyAssetViewModel);
        final PriceGateway gateway = new TwelveDataPriceGateway(TWELVE_DATA_API_KEY);
        final GetPriceInputBoundary interactor = new GetPriceInteractor(gateway, presenter);
        final GetPriceController controller = new GetPriceController(interactor);
        buyAssetView.setGetPriceController(controller);
        return this;
    }

    public AppBuilder addSellAssetView() {
        sellAssetViewModel = new SellAssetViewModel();
        sellAssetView = new SellAssetView(sellAssetViewModel);
        cardPanel.add(sellAssetView, sellAssetView.getViewName());
        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary =
            new SignupPresenter(viewManagerModel, signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor =
            new SignupInteractor(userDataAccessObject,
                signupOutputBoundary,
                userFactory,
                subAccountDataAccess);
        final SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(
            viewManagerModel,
            loggedInViewModel,
            loginViewModel,
            signupViewModel);

        final LoginInputBoundary loginInteractor = new LoginInteractor(
            userDataAccessObject,
            loginOutputBoundary,
            subAccountDataAccess
        );

        final LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addBuyAssetUseCase() {
        // --- DELETE THE OLD BLOCK THAT WAS HERE ---

        // Keep this new block (it has the updated transactionDataAccessObject)
        final BuyAssetPresenter presenter =
            new BuyAssetPresenter(buyAssetViewModel, loggedInViewModel, subAccountDataAccess);

        final BuyAssetInputBoundary interactor =
            new BuyAssetInteractor(
                subAccountDataAccess,
                transactionDataAccessObject,   // ⭐ This is the new part you likely wanted
                presenter
            );

        final BuyAssetController controller = new BuyAssetController(interactor, loggedInViewModel);
        buyAssetView.setBuyAssetController(controller);
        return this;
    }

    public AppBuilder addSellAssetUseCase() {
        // One presenter that implements BOTH SellAssetOutputBoundary
        // and SellAssetPriceOutputBoundary
        final SellAssetPresenter sellAssetPresenter =
            new SellAssetPresenter(sellAssetViewModel, loggedInViewModel, subAccountDataAccess);

        final SellAssetController sellAssetController = getSellAssetController(sellAssetPresenter);

        sellAssetView.setSellAssetController(sellAssetController);
        return this;
    }

    @NotNull
    private SellAssetController getSellAssetController(final SellAssetPresenter sellAssetPresenter) {
        final SellAssetInputBoundary sellAssetInteractor =
            new SellAssetInteractor(
                subAccountDataAccess,   // implements SellAssetDataAccessInterface
                transactionDataAccessObject,  // ⭐ SAME DAO used in BuyAssetInteractor
                sellAssetPresenter,     // SellAssetOutputBoundary
                sellAssetPresenter      // SellAssetPriceOutputBoundary
            );

        final SellAssetController sellAssetController =
            new SellAssetController(sellAssetInteractor, loggedInViewModel);
        return sellAssetController;
    }

    public AppBuilder addSwitchExchangeUseCase() {
        final SwitchExchangeOutputBoundary switchExchangeOutputBoundary = new SwitchExchangePresenter(
            exchangeViewModel,
            viewManagerModel);

        final SwitchExchangeInputBoundary switchExchangeInteractor = new SwitchExchangeInteractor(
            switchExchangeOutputBoundary);

        final SwitchExchangeController switchExchangeController =
            new SwitchExchangeController(switchExchangeInteractor);
        loggedInView.setSwitchExchangeController(switchExchangeController);
        return this;
    }

    public AppBuilder addSwitchTransferUseCase() {
        final SwitchTransferOutputBoundary switchTransferOutputBoundary = new SwitchTransferPresenter(
            transferViewModel,
            viewManagerModel);

        final SwitchTransferInputBoundary switchTransferInteractor = new SwitchTransferInteractor(
            switchTransferOutputBoundary, subAccountDataAccess);

        final SwitchTransferController switchTransferController =
            new SwitchTransferController(switchTransferInteractor);
        loggedInView.setSwitchTransferController(switchTransferController);
        return this;
    }

    public AppBuilder addTransferUseCase() {
        final TransferOutputBoundary transferOutputBoundary = new TransferPresenter(
            transferViewModel, loggedInViewModel, viewManagerModel);

        final TransferInputBoundary transferInteractor = new TransferInteractor(
            subAccountDataAccess,          // implements TransferDataAccessInterface
            transferOutputBoundary,
            transactionDataAccessObject    // 👈 SAME field you pass to BuyAssetInteractor
        );

        final TransferController transferController = new TransferController(transferInteractor);
        transferView.setTransferController(transferController);
        return this;
    }


    public AppBuilder addSwitchHistoryUseCase() {
        final SwitchHistoryOutputBoundary switchHistoryOutputBoundary = new SwitchHistoryPresenter(
            historyViewModel,
            viewManagerModel);

        final SwitchHistoryInputBoundary switchHistoryInteractor = new SwitchHistoryInteractor(
            switchHistoryOutputBoundary);

        final SwitchHistoryController switchHistoryController = new SwitchHistoryController(switchHistoryInteractor);
        loggedInView.setSwitchHistoryController(switchHistoryController);
        return this;
    }

    public AppBuilder addSwitchBuyAssetUseCase() {
        final SwitchBuyAssetOutputBoundary switchBuyAssetOutputBoundary = new SwitchBuyAssetPresenter(
            buyAssetViewModel,
            viewManagerModel);

        final SwitchBuyAssetInputBoundary switchBuyAssetInteractor = new SwitchBuyAssetInteractor(
            switchBuyAssetOutputBoundary);

        final SwitchBuyAssetController switchBuyAssetController =
            new SwitchBuyAssetController(switchBuyAssetInteractor);
        loggedInView.setSwitchBuyAssetController(switchBuyAssetController);
        return this;
    }

    public AppBuilder addSwitchSellAssetUseCase() {
        final SwitchSellAssetOutputBoundary switchSellAssetOutputBoundary = new SwitchSellAssetPresenter(
            sellAssetViewModel,
            viewManagerModel);

        final SwitchSellAssetInputBoundary switchSellAssetInteractor = new SwitchSellAssetInteractor(
            switchSellAssetOutputBoundary, subAccountDataAccess);

        final SwitchSellAssetController switchSellAssetController =
            new SwitchSellAssetController(switchSellAssetInteractor);
        loggedInView.setSwitchSellAssetController(switchSellAssetController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase() {
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
            loggedInViewModel,
            viewManagerModel,
            subAccountDataAccess);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
            switchLoggedInOutputBoundary,
            subAccountDataAccess);

        final SwitchLoggedInController switchLoggedInController =
            new SwitchLoggedInController(switchLoggedInInteractor);
        exchangeView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase2() {
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
            loggedInViewModel,
            viewManagerModel,
            subAccountDataAccess);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
            switchLoggedInOutputBoundary,
            subAccountDataAccess);

        final SwitchLoggedInController switchLoggedInController =
            new SwitchLoggedInController(switchLoggedInInteractor);
        transferView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase3() {
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
            loggedInViewModel,
            viewManagerModel,
            subAccountDataAccess);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
            switchLoggedInOutputBoundary,
            subAccountDataAccess);

        final SwitchLoggedInController switchLoggedInController =
            new SwitchLoggedInController(switchLoggedInInteractor);
        historyView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase4() {
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
            loggedInViewModel,
            viewManagerModel,
            subAccountDataAccess);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
            switchLoggedInOutputBoundary,
            subAccountDataAccess);

        final SwitchLoggedInController switchLoggedInController =
            new SwitchLoggedInController(switchLoggedInInteractor);
        buyAssetView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase5() {
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
            loggedInViewModel,
            viewManagerModel,
            subAccountDataAccess);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
            switchLoggedInOutputBoundary,
            subAccountDataAccess);

        final SwitchLoggedInController switchLoggedInController =
            new SwitchLoggedInController(switchLoggedInInteractor);
        sellAssetView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
            loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
            new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        final ChangePasswordController changePasswordController =
            new ChangePasswordController(changePasswordInteractor);
        loggedInView.setChangePasswordController(changePasswordController);
        return this;
    }

    public AppBuilder addExchangeUseCase() {
        final ExchangeOutputBoundary exchangeOutputBoundary =
            new ExchangePresenter(exchangeViewModel, loggedInViewModel);

        final ExchangeInputBoundary exchangeInteractor =
            new ExchangeInteractor(
                exchangeOutputBoundary,
                subAccountDataAccess,
                transactionDataAccessObject
            );

        final ExchangeController exchangeController =
            new ExchangeController(exchangeInteractor, loggedInViewModel);


        exchangeView.setExchangeController(exchangeController);
        return this;
    }

    public AppBuilder addDeleteSubAccountUseCase() {
        final DeleteSubAccountOutputBoundary presenter =
            new DeleteSubAccountPresenter(loggedInViewModel);
        final DeleteSubAccountInputBoundary interactor =
            new DeleteSubAccountInteractor(subAccountDataAccess, presenter);
        final DeleteSubAccountController controller =
            new DeleteSubAccountController(interactor);
        loggedInView.setDeleteSubAccountController(controller);
        return this;
    }

    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
            loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
            new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }

    public AppBuilder addCreateSubAccountUseCase() {
        final CreateSubAccountOutputBoundary outputBoundary =
            new CreateSubAccountPresenter(loggedInViewModel);
        final CreateSubAccountInputBoundary interactor =
            new CreateSubAccountInteractor(subAccountDataAccess, outputBoundary);
        final CreateSubAccountController controller =
            new CreateSubAccountController(interactor);
        loggedInView.setCreateSubAccountController(controller);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Banking Simulation");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(signupView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}
