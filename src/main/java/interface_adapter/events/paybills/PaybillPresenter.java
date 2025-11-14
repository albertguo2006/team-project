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
    private final ViewManagerModel viewManagerModel;

    public PaybillPresenter(PaybillViewModel paybillViewModel, ViewManagerModel viewManagerModel) {
        this.paybillViewModel = paybillViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(PaybillOutputData paybillOutputData) {
        PaybillState state = paybillViewModel.getState();
        if (state == null) {
            state = new PaybillState();
        }

        state.setSuccessMessage(paybillOutputData.getMessage());
        state.setAmountPaid(paybillOutputData.getAmount());
        state.setPaymentSuccess(true);
        state.setErrorMessage(null);

        paybillViewModel.setState(state);
        paybillViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(PaybillOutputData paybillOutputData) {
        PaybillState state = paybillViewModel.getState();
        if (state == null) {
            state = new PaybillState();
        }

        state.setErrorMessage(paybillOutputData.getMessage());
        state.setSuccessMessage(null);
        state.setAmountDue(paybillOutputData.getAmount());
        state.setPaymentSuccess(false);

        paybillViewModel.setState(state);
        paybillViewModel.firePropertyChange();
    }
}
