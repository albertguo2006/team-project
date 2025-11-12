package interface_adapter.events.paybills;

import use_case.paybills.PaybillInputBoundary;

/**
 * The Controller for the Paybill Use Case
 */

public class PaybillController {

    private final PaybillInputBoundary paybillUseCaseInteractor;

    public PaybillController(PaybillInputBoundary paybillUseCaseInteractor) {
        this.paybillUseCaseInteractor = paybillUseCaseInteractor;
    }

    /**
     * Executes the Paybill Use Case
     */
    public void execute() {
        paybillUseCaseInteractor.execute();
    }
}
