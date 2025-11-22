package use_case.stock_game.play_stock_game;

/**
 * input Boundary for the PLAY stock game use case.
 */
public interface PlayStockGameInputBoundary {

    /**
     * execute the PLAY stock game use case.
     */
    void execute(PlayStockGameInputData inputData);
}
