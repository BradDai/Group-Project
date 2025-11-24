package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addExchangeView()
                .addTransferView()
                .addHistoryView()
                .addBuyAssetView()
                .addSellAssetView()
                .addGetPriceUseCase()
                .addBuyAssetUseCase()
                .addSignupUseCase()
                .addLoginUseCase()
                .addSellAssetUseCase()
                .addExchangeUseCase()
                .addTransferUseCase()
                .addTransactionHistoryUsecase()
                .addCreateSubAccountUseCase()
                .addDeleteSubAccountUseCase()
                .addChangePasswordUseCase()
                .addLogoutUseCase()
                .addSwitchLoggedInUseCase()
                .addSwitchLoggedInUseCase2()
                .addSwitchLoggedInUseCase3()
                .addSwitchLoggedInUseCase4()
                .addSwitchLoggedInUseCase5()
                .addSwitchExchangeUseCase()
                .addSwitchTransferUseCase()
                .addSwitchHistoryUseCase()
                .addSwitchBuyAssetUseCase()
                .addSwitchSellAssetUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}