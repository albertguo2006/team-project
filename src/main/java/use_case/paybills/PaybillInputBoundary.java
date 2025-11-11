package use_case.paybills;

/**
 * Input Boundary for actions related to paying bills.
 */

public interface PaybillInputBoundary {

    /**
     * Executes the paybill use case
     */
    void execute(PaybillInputData paybillInputData);
}
