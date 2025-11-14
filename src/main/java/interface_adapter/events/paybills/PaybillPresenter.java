package interface_adapter.events.paybills;

import io.opencensus.stats.ViewManager;
import use_case.paybills.PaybillInputBoundary;
import use_case.paybills.PaybillOutputBoundary;
import use_case.paybills.PaybillOutputData;

/**
 * The Presenter for the Paybill Use Case.
 */

public class PaybillPresenter implements PaybillOutputBoundary {
    private final PaybillViewModel paybillViewModel;
    private final ViewManager viewManager;

    public PaybillPresenter(PaybillViewModel paybillViewModel, ViewManager viewManager) {
        this.paybillViewModel = paybillViewModel;
        this.viewManager = viewManager;
    }

    @Override
    public void prepareSuccessView(PaybillOutputData paybillOutputData) {
        PaybillState state = paybillViewModel.getState();
        state.setSuccessMessage(paybillOutputData.getMessage());
        state.setAmountPaid(paybillOutputData.getAmount());
        state.setPaymentSuccess(true);
        state.setErrorMessage(null);

        paybillViewModel.setState(state);
        paybillViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailureView(PaybillOutputData paybillOutputData) {
        PaybillState state = paybillViewModel.getState();
        state.setErrorMessage(paybillOutputData.getMessage());
        state.setSuccessMessage(null);
        state.setAmountDue(paybillOutputData.getAmount());
        state.setPaymentSuccess(false);

        paybillViewModel.setState(state);
        paybillViewModel.firePropertyChanged();
    }
}
