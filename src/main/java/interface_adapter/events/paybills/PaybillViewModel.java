package interface_adapter.events.paybills;

import interface_adapter.ViewModel;

/**
 * The View Model for the Paybill View
 */

public class PaybillViewModel extends ViewModel<PaybillState> {
    private PaybillState state = new PaybillState();

    public PaybillViewModel() {
        super("paybills");
    }

    public PaybillState getState() {
        return state;
    }

    public void setState(PaybillState state) {
        this.state = state;
    }
}
