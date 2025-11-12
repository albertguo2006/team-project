package use_case.paybills;

/**
 * The output boundary for the Paybill Use Case.
 */

public interface PaybillOutputBoundary {
    /**
     * Prepares the success view for the Paybill Use Case
     * When a single bill or all bills are successfully paid
     * i.e. when player balance is more or equal than the total due
     */
    void prepareSuccessView(PaybillOutputData paybillOutputData);

    /**
     * Prepares the failure view for the Paubill Use Case.
     * When the player fails to pay a single or all bills due to insufficient balance.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailureView(String errorMessage);
}
