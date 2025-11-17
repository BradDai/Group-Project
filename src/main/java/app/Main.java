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
                .addSignupUseCase()
                .addLoginUseCase()
                .addSwitchExchangeUseCase()
                .addSwitchTransferUseCase()
                .addSwitchHistoryUseCase()
                .addSwitchBuyAssetUseCase()
                .addSwitchSellAssetUseCase()
                .addSwitchLoggedInUseCase()
                .addSwitchLoggedInUseCase2()
                .addSwitchLoggedInUseCase3()
                .addSwitchLoggedInUseCase4()
                .addSwitchLoggedInUseCase5()
                .addChangePasswordUseCase()
                .addLogoutUseCase()
                .addExchangeUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
