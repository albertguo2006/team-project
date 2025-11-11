package use_case.paybills;

import entity.Bill;

import java.util.List;

public interface PaybillDataAccessInterface {

    /**
     * Returns the bills not yet paid by the player
     */
    List<Bill> getUnpaidBills();
    /**
     * Returns all the bills the player has
     */
    List<Bill> getALLBills();

}
