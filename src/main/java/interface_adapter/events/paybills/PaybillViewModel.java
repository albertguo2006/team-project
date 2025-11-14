package interface_adapter.events.paybills;

import interface_adapter.events.ViewModel;

/**
 * The View Model for the Paybill View
 */

public class PaybillViewModel extends ViewModel {
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
