package use_case.paybills;
import entity.Bill;
import entity.Player

import java.util.ArrayList;
import java.util.List;

/**
 * PaybillUseCase handles the player paying bills
 *
 * Responsibilities:
 * - Maintains a list of bills that are paid or unpaid
 * - Allows player to pay all bills at once or to pay one single bill
 * - Generates a notification for player about which bill the player needs to pay (maybe)
 * - Updates the player's balance based on bills paid
 *
 * This use case follows Clean Architecture principles ...
 */

public class PaybillUseCase {

    private final Player player;
    private List<Bill> bills;

    public PaybillUseCase(Player player,  List<Bill> bills) {
        this.player = player;
        this.bills = bills;
    }
    public double calculateTotalDue(){
        double total = 0;
        for (Bill bill: bills){
            if (!bill.getPaid()){
                total += bill.getAmount();
            }
        }
        return total;
    }
    public boolean payAllBills(){
        double totalDue = calculateTotalDue();
        if (player.getBalance() < totalDue){
            return false; // Insufficient funds
        }
        for  (Bill bill : bills){
            if (!bill.getPaid()){
                bill.setIsPaid(true);
            }
        }
        player.setBalance(player.getBalance() - totalDue);
        return true; //success
    }
    public boolean paySingleBill(String id){
        Bill billToPay = null;
        for (Bill bill: bills){
            if (bill.getId().equals(id)){
                billToPay = bill;
                break;
            }
        }
        if (billToPay == null()){
            return false; // bill not found
        }
        else if (billToPay.getPaid()){
            return false; // bill already paid
        }
        if (player.getBalance() < billToPay.getAmount()){
            return false; // insufficient funds
        }
        billToPay.setIsPaid(true);
        player.setBalance(player.getBalance() - billToPay.getAmount());
        return true;
    }

    public List<Bill> getAllBills(){
        return new ArrayList<>(bills);
    }
    public List<Bill> getUnpaidBills(){
        List<Bill> unpaidbills = new ArrayList<>();
        for (Bill bill: bills){
            if (!bill.getPaid()){
                unpaidbills.add(bill);
            }
        }
        return unpaidbills;
    }

}

