package use_case.stock_game.play_stock_game;

/**
 * The output boundary for the PLAY stock game Use Case.
 */
public interface PlayStockGameOutputBoundary {
    /**
     * Prepares the success view for the PLAY stock game Use Case.
     * for when the amount the player invests is right
     * @param outputData the output data
     */
    void prepareSuccessView(PlayStockGameOutputData outputData);

}