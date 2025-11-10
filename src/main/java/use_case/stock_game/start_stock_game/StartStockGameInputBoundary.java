package use_case.stock_game.start_stock_game;

/**
 * Input Boundary for actions related to the stock game.
 */
public interface StartStockGameInputBoundary {

    /**
     * Executes the stock game use case.
     * @param startStockGameInputData the input data
     */
    void execute(StartStockGameInputData startStockGameInputData);
}
