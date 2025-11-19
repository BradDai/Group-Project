package view;

import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View Manager for the program. It listens for property change events
 * in the ViewManagerModel and updates which View should be visible.
 */
public class ViewManager implements PropertyChangeListener {
    private final CardLayout cardLayout;
    private final JPanel views;
    private final ViewManagerModel viewManagerModel;

    public ViewManager(JPanel views, CardLayout cardLayout, ViewManagerModel viewManagerModel) {
        this.views = views;
        this.cardLayout = cardLayout;
        this.viewManagerModel = viewManagerModel;
        this.viewManagerModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final String viewModelName = (String) evt.getNewValue();
            cardLayout.show(views, viewModelName);

            // --- FIX: Dynamic Resizing Logic ---
            Window window = SwingUtilities.getWindowAncestor(views);
            if (window != null) {

                // 1. Find the active component in the CardLayout
                Component activeComponent = null;
                for (Component comp : views.getComponents()) {
                    // Check if this component corresponds to the active view name
                    // We rely on setName() being called in the View's constructor
                    if (viewModelName.equals(comp.getName())) {
                        activeComponent = comp;
                        break;
                    }
                }

                // 2. Handle sizing
                if (activeComponent instanceof TransferView) {
                    // Changed from 1000x800 to 600x600 - sufficient for content, not huge
                    activeComponent.setPreferredSize(new Dimension(600, 600));
                } else if (activeComponent != null) {
                    // Reset others to null (or a standard size) so pack() shrinks them
                    activeComponent.setPreferredSize(null);
                }

                // 3. Pack the window to fit the active component
                window.pack();
                window.setLocationRelativeTo(null); // Keep centered
            }
            // -----------------------------------
        }
    }
}