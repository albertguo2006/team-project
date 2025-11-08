package use_case.stock_game;

/**
 * Input Boundary for actions related to the stock game.
 */
public interface StockGameInputBoundary {

    /**
     * Executes the stock game use case.
     * @param stockGameInputData the input data
     */
    void execute(StockGameInputData stockGameInputData);
}
