package use_case.paybills;
import entity.Bill;
import entity.Player

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
        return bills.stream();
        bills.filter(bill-> !bill.getPaid());
        bills.mapToDouble(Bill::getAmount);
        bills.sum();
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
            player.setBalance(player.getBalance() - totalDue);
            return true; //success
        }
    }

}

