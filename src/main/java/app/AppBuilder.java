package app;

import data_access.FileSubAccountDataAccessJSON;
import data_access.FileUserDataAccessObject;
import data_access.FileSubAccountDataAccess;
import entity.UserFactory;
import interface_adapter.SwitchLoggedInController;
import interface_adapter.SwitchLoggedInPresenter;
import interface_adapter.ViewManagerModel;
import interface_adapter.buyasset.BuyAssetViewModel;
import interface_adapter.exchange.*;
import interface_adapter.history.HistoryViewModel;
import interface_adapter.logged_in.*;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.sell_asset.SellAssetController;
import interface_adapter.sell_asset.SellAssetPresenter;
import interface_adapter.sell_asset.SellAssetViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.subaccount.delete.DeleteSubAccountController;
import interface_adapter.subaccount.delete.DeleteSubAccountPresenter;
import interface_adapter.transfer.TransferViewModel;
import interface_adapter.subaccount.create.CreateSubAccountController;
import interface_adapter.subaccount.create.CreateSubAccountPresenter;
import use_case.SubAccount.delete.DeleteSubAccountInputBoundary;
import use_case.SubAccount.delete.DeleteSubAccountInteractor;
import use_case.SubAccount.delete.DeleteSubAccountOutputBoundary;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.exchange.*;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.SubAccount.SubAccountDataAccessInterface;
import use_case.sell_asset.SellAssetInputBoundary;
import use_case.sell_asset.SellAssetInteractor;
import use_case.sell_asset.SellAssetOutputBoundary;
import use_case.sell_asset.SellAssetPriceOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.switch_buyasset.SwitchBuyAssetInputBoundary;
import use_case.switch_buyasset.SwitchBuyAssetInteractor;
import use_case.switch_buyasset.SwitchBuyAssetOutputBoundary;
import use_case.switch_exchange.SwitchExchangeInputBoundary;
import use_case.switch_exchange.SwitchExchangeInteractor;
import use_case.switch_exchange.SwitchExchangeOutputBoundary;
import use_case.switch_history.SwitchHistoryInputBoundary;
import use_case.switch_history.SwitchHistoryInteractor;
import use_case.switch_history.SwitchHistoryOutputBoundary;
import use_case.switch_loggedin.SwitchLoggedInInputBoundary;
import use_case.switch_loggedin.SwitchLoggedInInteractor;
import use_case.switch_loggedin.SwitchLoggedInOutputBoundary;
import use_case.switch_sellasset.SwitchSellAssetInputBoundary;
import use_case.switch_sellasset.SwitchSellAssetInteractor;
import use_case.switch_sellasset.SwitchSellAssetOutputBoundary;
import use_case.switch_transfer.SwitchTransferInputBoundary;
import use_case.switch_transfer.SwitchTransferInteractor;
import use_case.switch_transfer.SwitchTransferOutputBoundary;
import use_case.transaction_history.TransactionHistoryInputBoundary;
import use_case.transaction_history.TransactionHistoryInteractor;
import use_case.transaction_history.TransactionHistoryOutputBoundary;
import use_case.SubAccount.SubAccountDataAccessInterface;
import use_case.SubAccount.create.CreateSubAccountInputBoundary;
import use_case.SubAccount.create.CreateSubAccountInteractor;
import use_case.SubAccount.create.CreateSubAccountOutputBoundary;
import view.*;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    // DAO version using local file storage
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);
    private final SubAccountDataAccessInterface subAccountDataAccess =
            new FileSubAccountDataAccessJSON("subaccounts.json");    // final DBUserDataAccessObject userDataAccessObject = new DBUserDataAccessObject(userFactory);

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
        TransactionHistoryOutputBoundary presenter =
                new interface_adapter.transaction_history.TransactionHistoryPresenter(historyViewModel);  // HistoryViewModel

        TransactionHistoryInputBoundary interactor =
                new TransactionHistoryInteractor(presenter);

        interface_adapter.transaction_history.TransactionHistoryController controller =
                new interface_adapter.transaction_history.TransactionHistoryController(interactor);

        historyView.setTransactionHistoryController(controller); //
        return this;
    }

    public AppBuilder addBuyAssetView() {
        buyAssetViewModel = new BuyAssetViewModel();
        buyAssetView = new BuyAssetView(buyAssetViewModel);
        cardPanel.add(buyAssetView, buyAssetView.getViewName());
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
        SignupController controller = new SignupController(userSignupInteractor);
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

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addSellAssetUseCase() {
        final SellAssetOutputBoundary sellAssetOutputBoundary =
                new SellAssetPresenter(sellAssetViewModel);
        final SellAssetPriceOutputBoundary sellAssetPriceOutputBoundary =
                new SellAssetPresenter(sellAssetViewModel);

        final SellAssetInputBoundary sellAssetInteractor =
                new SellAssetInteractor(
                        sellAssetOutputBoundary,
                        sellAssetPriceOutputBoundary
                );

        final SellAssetController sellAssetController = new SellAssetController(sellAssetInteractor);
        sellAssetView.setSellAssetController(sellAssetController);
        return this;
    }

    public AppBuilder addSwitchExchangeUseCase() {
        final SwitchExchangeOutputBoundary switchExchangeOutputBoundary = new SwitchExchangePresenter(
                exchangeViewModel,
                viewManagerModel);

        final SwitchExchangeInputBoundary switchExchangeInteractor = new SwitchExchangeInteractor(
                switchExchangeOutputBoundary);

        SwitchExchangeController switchExchangeController = new SwitchExchangeController(switchExchangeInteractor);
        loggedInView.setSwitchExchangeController(switchExchangeController);
        return this;
    }

    public AppBuilder addSwitchTransferUseCase() {
        final SwitchTransferOutputBoundary switchTransferOutputBoundary = new SwitchTransferPresenter(
                transferViewModel,
                viewManagerModel);

        final SwitchTransferInputBoundary switchTransferInteractor = new SwitchTransferInteractor(
                switchTransferOutputBoundary);

        SwitchTransferController switchTransferController = new SwitchTransferController(switchTransferInteractor);
        loggedInView.setSwitchTransferController(switchTransferController);
        return this;
    }

    public AppBuilder addSwitchHistoryUseCase() {
        final SwitchHistoryOutputBoundary switchHistoryOutputBoundary = new SwitchHistoryPresenter(
                historyViewModel,
                viewManagerModel);

        final SwitchHistoryInputBoundary switchHistoryInteractor = new SwitchHistoryInteractor(
                switchHistoryOutputBoundary);

        SwitchHistoryController switchHistoryController = new SwitchHistoryController(switchHistoryInteractor);
        loggedInView.setSwitchHistoryController(switchHistoryController);
        return this;
    }

    public AppBuilder addSwitchBuyAssetUseCase() {
        final SwitchBuyAssetOutputBoundary switchBuyAssetOutputBoundary = new SwitchBuyAssetPresenter(
                buyAssetViewModel,
                viewManagerModel);

        final SwitchBuyAssetInputBoundary switchBuyAssetInteractor = new SwitchBuyAssetInteractor(
                switchBuyAssetOutputBoundary);

        SwitchBuyAssetController switchBuyAssetController = new SwitchBuyAssetController(switchBuyAssetInteractor);
        loggedInView.setSwitchBuyAssetController(switchBuyAssetController);
        return this;
    }

    public AppBuilder addSwitchSellAssetUseCase() {
        final SwitchSellAssetOutputBoundary switchSellAssetOutputBoundary = new SwitchSellAssetPresenter(
                sellAssetViewModel,
                viewManagerModel);

        final SwitchSellAssetInputBoundary switchSellAssetInteractor = new SwitchSellAssetInteractor(
                switchSellAssetOutputBoundary);

        SwitchSellAssetController switchSellAssetController = new SwitchSellAssetController(switchSellAssetInteractor);
        loggedInView.setSwitchSellAssetController(switchSellAssetController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase() {
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
                loggedInViewModel,
                viewManagerModel);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
                switchLoggedInOutputBoundary);

        SwitchLoggedInController switchLoggedInController = new SwitchLoggedInController(switchLoggedInInteractor);
        exchangeView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase2(){
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
                loggedInViewModel,
                viewManagerModel);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
                switchLoggedInOutputBoundary);

        SwitchLoggedInController switchLoggedInController =
                new SwitchLoggedInController(switchLoggedInInteractor);
        transferView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase3() {
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
                loggedInViewModel,
                viewManagerModel);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
                switchLoggedInOutputBoundary);

        SwitchLoggedInController switchLoggedInController =
                new SwitchLoggedInController(switchLoggedInInteractor);
        historyView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase4() {
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
                loggedInViewModel,
                viewManagerModel);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
                switchLoggedInOutputBoundary);

        SwitchLoggedInController switchLoggedInController =
                new SwitchLoggedInController(switchLoggedInInteractor);
        buyAssetView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addSwitchLoggedInUseCase5() {
        final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary = new SwitchLoggedInPresenter(
                loggedInViewModel,
                viewManagerModel);

        final SwitchLoggedInInputBoundary switchLoggedInInteractor = new SwitchLoggedInInteractor(
                switchLoggedInOutputBoundary);

        SwitchLoggedInController switchLoggedInController =
                new SwitchLoggedInController(switchLoggedInInteractor);
        sellAssetView.setSwitchLoggedInController(switchLoggedInController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        loggedInView.setChangePasswordController(changePasswordController);
        return this;
    }

    public AppBuilder addExchangeUseCase() {
        final ExchangeOutputBoundary exchangeOutputBoundary = new ExchangePresenter(exchangeViewModel);

        final ExchangeInputBoundary ExchangeInteractor = new ExchangeInteractor(exchangeOutputBoundary);

        ExchangeController exchangeController = new ExchangeController(ExchangeInteractor);
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

    /**
     * Adds the Logout Use Case to the application.
     * @return this builder
     */
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
