package data_access;

import entity.Bill;
import use_case.paybills.PaybillDataAccessInterface;

import java.util.*;
import java.util.stream.Collectors;

public class PaybillDataAccessObject implements PaybillDataAccessInterface {
    private final Map<String, Bill> bills = new HashMap<>();
    private int currentWeek = 1;

    // Available bill types for random selection
    private final Bill.BillType[] billtypes = Bill.BillType.values();

    // Bill name mappings for each type
    private final Map<Bill.BillType, String> billNames = Map.ofEntries(
            Bill.BillType.RENT, "Rent",
            Bill.BillType.CREDIT_CARD, "Credit Card Payment",
            Bill.BillType.INSURANCE, "Insurance Premium",
            Bill.BillType.TAX, "Property Tax",
            Bill.BillType.SUBSCRIPTION, "Streaming Subscription",
            Bill.BillType.MORTGAGE, "Mortgage Payment",
            Bill.BillType.ELECTRICITY, "Electricity Bill",
            Bill.BillType.INTERNET, "Internet Service",
            Bill.BillType.CELLPHONE, "Cell Phone Bill",
            Bill.BillType.WATER, "Water Bill",
            Bill.BillType.GAS, "Gas Bill",
            Bill.BillType.LOAN, "Loan Payment"
    );

    //Amount ranges for each bill type (min, max)

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
    public Bill getBillById(String id) {
        return bills.get(id);
        }
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

    @Override
    public void advanceToNextWeek() {
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


    private Date calculateDueDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return  cal.getTime();
    }
}
