package use_case.paybills;
import entity.Player;

/**
 * PaybillUseCase handles the player paying its bills
 *
 * Responsibilities:
 * - Maintains a list of bills that were paid / unpaid
 * - Generates a notification for player about which bill the player needs to pay
 * - Updates the player's money based on how much was spent to pay bills
 *
 * This use case follows Clean Architecture principles ...
 */

public class PaybillUseCase {

    private final Player player;
    public PaybillUseCase(Player player) {
        this.player = player;
    }
    public void paybills(Player player, double amount){}

}

