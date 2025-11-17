package interface_adapter.transfer;

import interface_adapter.ViewModel;

public class TransferViewModel extends ViewModel<TransferState> {

    public TransferViewModel() {
        super("transfer");
        setState(new TransferState());
    }
}
