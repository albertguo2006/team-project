package use_case.stock_game.start_stock_game;

/**
 * DAO interface for the START stock game use case.
 */

public interface StartStockGameDataAccessInterface {

    /**
     * Returns the user with the given username.
     * @param username the username to look up
     * @return the user with the given username
     */
    User get(String username);





}
