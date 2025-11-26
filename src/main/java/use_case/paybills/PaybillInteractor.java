package use_case.paybills;

import java.util.List;

import entity.Bill;
import entity.Player;

public class PaybillInteractor implements PaybillInputBoundary {
    private final PaybillDataAccessInterface paybillDataAccess;
    private final PaybillOutputBoundary paybillPresenter;
    private final Player player;

    public PaybillInteractor(PaybillDataAccessInterface paybillDataAccess, PaybillOutputBoundary paybillPresenter,
                             Player player) {
        this.paybillDataAccess = paybillDataAccess;
        this.paybillPresenter = paybillPresenter;
        this.player = player;
    }

    @Override
    public void payAllBills() {
        List<Bill> unpaidBills = paybillDataAccess.getUnpaidBills();
        double totalDue = calculateTotalDue(unpaidBills);

        if (player.getBalance() >= totalDue) {
            // Deduct money first
            player.setBalance(player.getBalance() - totalDue);
            System.out.println("PaybillInteractor: Deducted $" + String.format("%.2f", totalDue) + " from player balance");
            System.out.println("PaybillInteractor: New balance: $" + String.format("%.2f", player.getBalance()));
            
            // Mark bills as paid and save
            for (Bill bill : unpaidBills) {
                bill.setIsPaid(true);
                paybillDataAccess.saveBill(bill);
                System.out.println("PaybillInteractor: Paid bill: " + bill.getName());
            }
            
            PaybillOutputData outputData = new PaybillOutputData(true, "All bills paid successfully!", totalDue);
            paybillPresenter.prepareSuccessView(outputData);
        }
        else {
            PaybillOutputData outputData = new PaybillOutputData(false, "Insufficient funds!", totalDue);
            paybillPresenter.prepareFailureView(outputData);
        }
    }

    private double calculateTotalDue(List<Bill> unpaidBills) {
        double total = 0;
        for (Bill bill : unpaidBills) {
            total += bill.getAmount();
        }
        return total;
    }

    @Override
    public void paySingleBill(String id) {
        List<Bill> allBills = paybillDataAccess.getAllBills();
        Bill billToPay = findBillById(allBills, id);

        if (billToPay == null){
            PaybillOutputData outputData = new PaybillOutputData(false, "Bill not found!", 0);
            paybillPresenter.prepareFailureView(outputData);
            return;
        }
        else if (billToPay.getPaid()){
            PaybillOutputData outputData = new PaybillOutputData(false, "Bill already paid: " + billToPay.getName(), 0);
            paybillPresenter.prepareFailureView(outputData);
            return;
        }
        else if (player.getBalance() < billToPay.getAmount()){
            PaybillOutputData outputData = new PaybillOutputData(false, "Insufficient funds to pay " + billToPay.getName(), billToPay.getAmount());
            paybillPresenter.prepareFailureView(outputData);
            return;
        }
        
        // Deduct money first
        double amount = billToPay.getAmount();
        player.setBalance(player.getBalance() - amount);
        System.out.println("PaybillInteractor: Deducted $" + String.format("%.2f", amount) + " from player balance");
        System.out.println("PaybillInteractor: New balance: $" + String.format("%.2f", player.getBalance()));
        
        // Mark bill as paid and save
        billToPay.setIsPaid(true);
        paybillDataAccess.saveBill(billToPay);
        System.out.println("PaybillInteractor: Paid bill: " + billToPay.getName());
        
        PaybillOutputData outputData = new PaybillOutputData(true, "Paid: " + billToPay.getName(), amount);
        paybillPresenter.prepareSuccessView(outputData);
    }

    private Bill findBillById(List<Bill> allBills, String id) {
        for (Bill bill : allBills) {
            if  (bill.getId().equals(id)) {
                return bill;
            }
        }
        return null;
    }
}
