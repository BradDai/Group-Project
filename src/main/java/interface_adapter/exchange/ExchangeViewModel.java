package interface_adapter.exchange;

import interface_adapter.ViewModel;

public class ExchangeViewModel extends ViewModel<ExchangeState> {

    public ExchangeViewModel() {
        super("exchange");
        setState(new ExchangeState());
    }

}
