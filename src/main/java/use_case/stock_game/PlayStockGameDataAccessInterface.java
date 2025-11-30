package use_case.stock_game;

import java.util.List;
import java.util.Map;

/**
 * DAO interface for the PLAY stock game use case.
 */

public interface PlayStockGameDataAccessInterface {

    /**
     * Returns list of stock prices (legacy method).
     * @param symbol the username to look up
     * @param day the day in the game
     * @return list of the prices for the respective stock and day
     * @throws Exception if there is an error  (like day is not in 1-5, or symbol DNE)
     */
    List<Double> getIntradayPrices(String symbol, int day) throws Exception;

    /**
     * Returns stock prices for 5 consecutive days starting from a specific day.
     * @param symbol the stock symbol
     * @param month the month in YYYY-MM format
     * @param startDayIndex the starting day index (0-based)
     * @return a map where keys are game day numbers (1-5) and values are lists of prices
     * @throws Exception if there is an error accessing the data
     */
    Map<Integer, List<Double>> getFiveDayPrices(String symbol, String month, int startDayIndex) throws Exception;

}
