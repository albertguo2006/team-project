package api;

import use_case.stock_game.play_stock_game.PlayStockGameDataAccessInterface;

import java.util.List;

// data accss object to get data from alpha vantage stock api
public class AlphaStockDataAccessObject implements PlayStockGameDataAccessInterface {

    /**
     * Returns list of stock prices.
     * @param symbol the stock symbol to look up
     * @param day the day in the game
     * @return list of the prices for the respective stock and day
     * @throws Exception if there is an error  (like day is not in 1-5, or symbol DNE)
     */
    @Override
    public List<Double> getIntradayPrices(String symbol, int day) throws Exception {
        return AlphaStockDataBase.getIntradayOpensForGameDay(symbol, day);
    }
}
