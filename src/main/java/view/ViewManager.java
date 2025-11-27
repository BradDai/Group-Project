package view;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import interfaceadapter.ViewManagerModel;

/**
 * The View Manager for the program. It listens for property change events
 * in the ViewManagerModel and updates which View should be visible.
 */
public class ViewManager implements PropertyChangeListener {
    private final CardLayout cardLayout;
    private final JPanel views;

    public ViewManager(final JPanel views, final CardLayout cardLayout, final ViewManagerModel viewManagerModel) {
        this.views = views;
        this.cardLayout = cardLayout;
        viewManagerModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final String viewModelName = (String) evt.getNewValue();
            cardLayout.show(views, viewModelName);

            final Window window = SwingUtilities.getWindowAncestor(views);
            if (window != null) {

                // Loop through all components to find active and reset inactive
                for (final Component comp : views.getComponents()) {
                    if (viewModelName.equals(comp.getName())) {
                        // This is the active component
                        if (comp instanceof TransferView) {
                            // Set a minimum size for TransferView
                            comp.setPreferredSize(new Dimension(600, 650));
                        }
                        else {
                            comp.setPreferredSize(null);
                        }
                    }
                    else {
                        comp.setPreferredSize(null);
                    }
                }

                // Pack window to fit the active component's size
                window.pack();
                window.setLocationRelativeTo(null);
            }
        }
    }
}
