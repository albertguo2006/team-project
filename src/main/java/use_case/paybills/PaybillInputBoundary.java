package use_case.paybills;

/**
 * Input Boundary for actions related to paying bills.
 */

public interface PaybillInputBoundary {

    /**
     * Pay all unpaid bills at once
     */
    void payAllbill();

    /**
     * Pay a specific bill
     */
    void paySingleBill(String id);
}
