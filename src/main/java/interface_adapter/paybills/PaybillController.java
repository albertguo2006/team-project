package interface_adapter.paybills;

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
     * Executes Pay All Bills
     * Called when the "Pay All" button is clicked
     */
    public void payAllBills() {
        paybillUseCaseInteractor.payAllBills();
    }

    /**
     * Executes Pay Single Bill
     * Called when an individual "Pay" button is clicked for a specific bill
     * @param id The ID of the bill to pay
     */
    public void paySingleBill(String id) {
        paybillUseCaseInteractor.paySingleBill(id);
    }
}
