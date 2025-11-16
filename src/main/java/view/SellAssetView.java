package view;

import interface_adapter.SwitchLoggedInController;
import interface_adapter.sellasset.SellAssetViewModel;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SellAssetView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "sellasset";
    private final SellAssetViewModel sellAssetViewModel;
    private SwitchLoggedInController switchLoggedInController;

    private final JButton back;

    public SellAssetView(SellAssetViewModel sellAssetViewModel) {
        this.sellAssetViewModel = sellAssetViewModel;
        this.sellAssetViewModel.addPropertyChangeListener(this);

        final JPanel Buttons = new  JPanel();
        back = new  JButton("Back");
        Buttons.add(back);
        this.add(Buttons);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        back.addActionListener(
                evt -> {
                    if(evt.getSource().equals(back)) {
                        switchLoggedInController.switchToLoggedInView();
                    }
                }
        );

    }

    public void actionPerformed(ActionEvent evt) { System.out.println("Click " + evt.getActionCommand()); }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public String getViewName() { return viewName; }

    public void setSwitchLoggedInController(SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }
}
