package use_case.stock_game.play_stock_game;

import java.util.List;

/**
 * DAO interface for the PLAY stock game use case.
 */

public interface PlayStockGameDataAccessInterface {

    /**
     * Returns list of stock prices.
     * @param symbol the username to look up
     * @param day the day in the game
     * @return list of the prices for the respective stock and day
     * @throws Exception if there is an error  (like day is not in 1-5, or symbol DNE)
     */
    List<Double> getIntradayPrices(String symbol, int day) throws Exception;

}
