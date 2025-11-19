package api;

import org.json.JSONException;

import java.util.List;

/**
 * StockDB is an interface that defines the methods that the stockDB class must implement.
 */
public interface StockDataBase {
    /**
     * A method that saves the stock prices in a JSON file named after the stock
     *
     * @param stockSymbol is the symbol of the stock.
     * @return the grade of the student in the course.
     * @throws JSONException if there is an error (if stock symbol DNE)
     */

    void getStockPrices(String stockSymbol) throws Exception;

    void saveToFile(String json, String symbol);

}