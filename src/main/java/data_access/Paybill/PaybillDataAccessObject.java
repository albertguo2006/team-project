package data_access.Paybill;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.Bill;
import use_case.paybills.PaybillDataAccessInterface;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class PaybillDataAccessObject implements PaybillDataAccessInterface {
    private final Map<String, Bill> bills = new HashMap<>();
    private int currentWeek = 1;

    // Available bill types for random selection
    private final Bill.BillType[] billTypes = Bill.BillType.values();

    // Bill name mappings for each type
    private final Map<Bill.BillType, String> billNames = Map.ofEntries(
            Map.entry(Bill.BillType.RENT, "Rent"),
            Map.entry(Bill.BillType.CREDIT_CARD, "Credit Card Payment"),
            Map.entry(Bill.BillType.INSURANCE, "Insurance Premium"),
            Map.entry(Bill.BillType.TAX, "Property Tax"),
            Map.entry(Bill.BillType.SUBSCRIPTION, "Streaming Subscription"),
            Map.entry(Bill.BillType.MORTGAGE, "Mortgage Payment"),
            Map.entry(Bill.BillType.ELECTRICITY, "Electricity Bill"),
            Map.entry(Bill.BillType.INTERNET, "Internet Service"),
            Map.entry(Bill.BillType.CELLPHONE, "Cell Phone Bill"),
            Map.entry(Bill.BillType.WATER, "Water Bill"),
            Map.entry(Bill.BillType.GAS, "Gas Bill"),
            Map.entry(Bill.BillType.LOAN, "Loan Payment"
    ));

    //Amount ranges for each bill type (min, max)
    private final Map<Bill.BillType, double[]> amountRange = Map.ofEntries(
            Map.entry(Bill.BillType.RENT, new double[]{1300.0, 1300.0}),
            Map.entry(Bill.BillType.CREDIT_CARD, new double[]{50.0, 2000.0}),
            Map.entry(Bill.BillType.INSURANCE, new double[]{150.0, 300.0}),
            Map.entry(Bill.BillType.TAX, new double[]{300.0, 800.0}),
            Map.entry(Bill.BillType.SUBSCRIPTION, new double[]{10.0, 20.0}),
            Map.entry(Bill.BillType.MORTGAGE, new double[]{1500.0, 2500.0}),
            Map.entry(Bill.BillType.ELECTRICITY, new double[]{80.0, 200.0}),
            Map.entry(Bill.BillType.INTERNET, new double[]{60.0, 100.0}),
            Map.entry(Bill.BillType.CELLPHONE, new double[]{40.0, 100.0}),
            Map.entry(Bill.BillType.WATER, new double[]{30.0, 80.0}),
            Map.entry(Bill.BillType.GAS, new double[]{50.0, 120.0}),
            Map.entry(Bill.BillType.LOAN, new double[]{400.0, 2000.0}
    ));

    public PaybillDataAccessObject() {
        loadBillsFromFile();
        if (bills.isEmpty()) {
            generateWeeklyBills(); // Only generate if no saved bills
        }
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
        saveBillsToFile();
    }

    private void saveBillsToFile(){
        try (FileWriter file = new FileWriter("bills.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(bills, file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bills to file", e);
        }
    }

    private void loadBillsFromFile(){
        File file = new File("bills.json");
        if (file.exists()){
            try (FileReader fileReader = new FileReader(file)){
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Bill>>(){}.getType();
                Map<String, Bill> loadedBills = gson.fromJson(fileReader, type);
                if (loadedBills != null){
                    bills.putAll(loadedBills);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Bills file not found: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Failed to load bills from file: " + e.getMessage());
            }
        }
    }

    @Override
    public Bill getBillById(String id) {
        return bills.get(id);
        }

    @Override
    public List<Bill> generateWeeklyBills(){
        Random rand = new Random();
        List<Bill> newBills = new ArrayList<>();

        // Generate 4-8 bills each week
        int numberOfBills = 4 + rand.nextInt(5);

        for (int i = 0; i < numberOfBills; i++) {
            // Select random bill type
            Bill.BillType billType = billTypes[rand.nextInt(billTypes.length)];

            // Generate unique bill id
            String id = generateBillId(billType, currentWeek, i);

            // Get bill name and amount range
            String billName = billNames.get(billType);
            double[] range = amountRange.get(billType);
            double amount = generateRandomAmount(range[0], range[1], rand);

            // Calculate due date (7 days from now)
            Date dueDate = calculateDueDate();

            // Create new bill (all bills start as unpaid)
            Bill bill = new Bill(id, amount, billName, dueDate, false, billType);
            bills.put(id, bill);
            newBills.add(bill);

        }
        System.out.println("Generated " + numberOfBills + " bills.");
        return newBills;
    }

    private String generateBillId(Bill.BillType billType, int week, int index){
        return billType.name().toLowerCase() + "_" + week + "_" + index + "_" + System.currentTimeMillis();
    }

    private double generateRandomAmount(double lowerBound, double upperBound, Random random){
        double amount = lowerBound + (upperBound - lowerBound) * random.nextDouble();
        return Math.round(amount * 100.0) / 100.0; // Round to 2 decimal places
    }

    @Override
    public void advanceToNextWeek() {
        // Mark all current bills as overdue
        int overdueCount = 0;
        for (Bill bill: bills.values()){
            if (!bill.getPaid()){
                overdueCount++;
                System.out.println("Bill overdue: " + bill.getName() + " - $" + bill.getAmount());
            }
        }
        if (overdueCount > 0){
            System.out.println(overdueCount + " bills marked as overdue");
        }

        // Generate new bills for the next week
        currentWeek++;
        generateWeeklyBills();
    }


    private Date calculateDueDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        return  cal.getTime();
    }
}
