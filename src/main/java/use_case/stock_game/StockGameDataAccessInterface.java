package use_case.stock_game;

/**
 * DAO interface for the stock game use case.
 */

public interface StockGameDataAccessInterface {

    /**
     * Returns the user with the given username.
     * @param username the username to look up
     * @return the user with the given username
     */
    User get(String username);





}
