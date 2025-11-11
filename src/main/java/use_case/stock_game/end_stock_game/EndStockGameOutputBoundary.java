package use_case.stock_game.end_stock_game;

/**
 * The output boundary for the END stock game Use Case.
 */
public interface EndStockGameOutputBoundary {
    /**
     * Prepares the success view for the stock game Use Case.
     * for when the amount the player invests is right
     * @param outputData the output data
     */
    void prepareSuccessView(EndStockGameOutputData outputData);


}