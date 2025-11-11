package use_case.stock_game.end_stock_game;

import entity.Player;

/**
 * DAO interface for the END stock game use case.
 */

public interface EndStockGameDataAccessInterface {

    /**
     * Returns the total equity of the player (earnings from the game).
     * @return the total equity of the player
     */
    Double getTotalEquity();

    /**
     * Updates the player balance to include the earnings
     * @param totalEquity the earnings
     * @param player the player which earned the money
     */
    // TODO: is there a specific method to update player balance or we do it manually
    // ex. player.balance += 100? etc
    void updateBalanceEarnings(Player player, Double totalEquity);
}
