package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Entry point for the banking application.
 */
public class Main {

    /**
     * Builds the app.
     * @param args arguments.
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final AppBuilder appBuilder = new AppBuilder();
            final JFrame application = appBuilder.build();

            application.pack();
            application.setLocationRelativeTo(null);
            application.setLocationRelativeTo(null);
            application.setVisible(true);
        });
    }
}
