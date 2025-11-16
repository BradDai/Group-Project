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
                .addSignupUseCase()
                .addLoginUseCase()
                .addSwitchExchangeUseCase()
                .addSwitchLoggedInUseCase()
                .addChangePasswordUseCase()
                .addLogoutUseCase()
                .addExchangeUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
