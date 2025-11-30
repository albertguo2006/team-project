package api;

import org.json.JSONException;

/**
 * StockDB is an interface that defines the methods that the stockDB class must implement.
 */
public interface StockDataBase {
    /**
     * A method that saves the stock prices in a JSON file named after the stock
     *
     * @param stockSymbol is the symbol of the stock.
     * @param month       the month of the data
     * @throws JSONException if there is an error (if stock symbol DNE)
     */
    void getStockPrices(String stockSymbol, String month) throws Exception;

    void saveToFile(String json, String symbol);

}