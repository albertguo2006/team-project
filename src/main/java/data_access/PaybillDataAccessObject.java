package data_access;

import entity.Bill;
import use_case.paybills.PaybillDataAccessInterface;

import java.util.*;
import java.util.stream.Collectors;

public class PaybillDataAccessObject implements PaybillDataAccessInterface {
    private final Map<String, Bill> bills = new HashMap<>();
    private int currentWeek = 1;

    // Templates???

    public PaybillDataAccessObject() {
        // Generate initial bills
        generateWeeklyBills();
    }

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
    public Bill getBillbyId(String id){
        return bills.get(id);
    }


    public List<Bill> generateWeeklyBills(){
        Random rand = new Random();
        List<Bill> newBills = new ArrayList<>();

        // Generate 4-7 bills each week
        int numberOfBills = 4 + rand.nextInt(5);

        for (int i = 0; i < numberOfBills; i++) {
            // TODO: Complete
        }
    }

    public void advanceToNextMonth(){
        // Mark all current bills as overdue
        bills.values().forEach(bill -> {
            if (!bill.getPaid()){
                System.out.println("Bill overdue: " + bill.getName());
            }
        });

        // Generate new bills for the next week
        currentWeek++;
        generateWeeklyBills();
    }

    private Date calculateDueDate(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return  cal.getTime();
    }
}
