package use_case.paybills;

import entity.Bill;
import entity.Player;

import java.util.List;

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
            for (Bill bill : unpaidBills) {
                bill.setIsPaid(true);
                paybillDataAccess.saveBill(bill);
            }
            player.setBalance(player.getBalance() - totalDue);
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
        billToPay.setIsPaid(true);
        paybillDataAccess.saveBill(billToPay);
        player.setBalance(player.getBalance() - billToPay.getAmount());
        PaybillOutputData outputData = new PaybillOutputData(true, "Paid: " + billToPay.getName(), billToPay.getAmount());
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
