package use_case.stock_game.play_stock_game;

/**
 * Input Boundary for actions related to the stock game.
 */
public interface PlayStockGameInputBoundary {

    /**
     * Executes the stock game use case.
     * @param playStockGameInputData the input data
     */
    void execute(PlayStockGameInputData playStockGameInputData);
}
