package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.exchange.ExchangeController;
import interface_adapter.exchange.ExchangeViewModel;
import interface_adapter.exchange.SwitchLoggedInController;
import interface_adapter.exchange.SwitchLoggedInPresenter;
import use_case.exchange.ExchangeInputBoundary;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ExchangeView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "exchange";
    private final ExchangeViewModel exchangeViewModel;
    private ExchangeController exchangeController;
    private SwitchLoggedInController switchLoggedInController;

    private final JButton back;

    public ExchangeView(ExchangeViewModel exchangeViewModel) {
        this.exchangeViewModel = exchangeViewModel;
        this.exchangeViewModel.addPropertyChangeListener(this);

        final JPanel Buttons = new JPanel();
        back = new JButton("Back");
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


    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public String getViewName() { return viewName; }

    public void setSwitchLoggedInController (SwitchLoggedInController switchLoggedInController) {
        this.switchLoggedInController = switchLoggedInController;
    }
}
