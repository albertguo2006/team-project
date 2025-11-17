package data_access.Paybill;

import entity.Bill;
import use_case.paybills.PaybillDataAccessInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryPaybillDataAccessObject implements PaybillDataAccessInterface {
    private final Map<String, Bill> bills = new HashMap<>();

    @Override
    public List<Bill> getUnpaidBills(){
        return bills.values().stream().filter(bill -> !bill.getPaid()).collect(Collectors.toList());
    }

    @Override
    public List<Bill> getAllBills(){
        return new ArrayList<>(bills.values());
    }

    @Override
    public void saveBill(Bill bill){
        bills.put(bill.getId(), bill);
        // TODO
    }

    @Override
    public Bill getBillById(String id) {
        return bills.get(id);
    }

    @Override
    public List<Bill> generateWeeklyBills(){
        return new ArrayList<>();
    }

    @Override
    public void advanceToNextWeek(){}
}
