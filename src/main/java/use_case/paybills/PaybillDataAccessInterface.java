package use_case.paybills;

import entity.Bill;

import java.util.List;

public interface PaybillDataAccessInterface {

    /**
     * Returns the bills not yet paid by the player
     */
    List<Bill> getUnpaidBills();

    /**
     * Returns all the bills the player has (both paid and unpaid)
     */
    List<Bill> getAllBills();

    /**
     * Saves the updated bill's information (for when the bill is paid)
     */
    void saveBill(Bill bill);

    /**
     * Gets a specific bill by ID
     */
    Bill getBillById(String id);

    /**
     * Generates new bills for the current period (weekly)
     */
    List<Bill> generateWeeklyBills();

    /**
     * Marks all current bills as paid and generates new ones
     * Used when advancing to a new week
     */
    void advanceToNextWeek();

}
