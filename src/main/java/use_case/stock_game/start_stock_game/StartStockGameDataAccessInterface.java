package use_case.stock_game.start_stock_game;

import entity.Player;

/**
 * DAO interface for the stock game use case.
 */

public interface StartStockGameDataAccessInterface {

    /**
     * Returns the player with the given username.
     * @param username the username to look up
     * @return the player with the given username
     */
    Player get(String username);

}
