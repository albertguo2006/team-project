package use_case.stock_game.start_stock_game;

/**
 * DAO interface for the START stock game use case.
 */

public interface StartStockGameDataAccessInterface {

    /**
     * Checks if the given startAmount is valid (in between 0 and player balance)
     * AND if the player has not already played the stock game for this game day
     * // TODO: how to check if player has already played stocks today
     *
     * @param startAmount the double to check
     * @param ??????
     * @return true if the user is able to begin the stock game
     */
    boolean validBeginGame(Double startAmount);

    void setStartAmount(Double startAmount);
}
