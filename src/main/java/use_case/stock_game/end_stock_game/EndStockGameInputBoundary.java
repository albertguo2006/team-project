package use_case.stock_game.end_stock_game;

/**
 * Input Boundary for actions related to the stock game.
 */
public interface EndStockGameInputBoundary {

    /**
     * Executes the stock game use case.
     * @param endStockGameInputData the input data
     */
    void execute(EndStockGameInputData endStockGameInputData);
}
