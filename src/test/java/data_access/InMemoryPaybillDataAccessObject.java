package data_access;

import data_access.Paybill.PaybillDataAccessObject;
import entity.Bill;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of PaybillDataAccessObject for testing purposes.
 * Returns empty bill lists to ensure deterministic test behavior.
 */
public class InMemoryPaybillDataAccessObject extends PaybillDataAccessObject {

    public InMemoryPaybillDataAccessObject() {
        // Don't call super() to avoid generating random bills
    }

    @Override
    public List<Bill> getUnpaidBills() {
        return new ArrayList<>();
    }

    @Override
    public List<Bill> getAllBills() {
        return new ArrayList<>();
    }

    @Override
    public void saveBill(Bill bill) {
        // No-op for testing
    }

    @Override
    public Bill getBillById(String id) {
        return null;
    }

    @Override
    public List<Bill> generateWeeklyBills() {
        return new ArrayList<>();
    }

    @Override
    public void advanceToNextWeek() {
        // No-op for testing
    }
}
