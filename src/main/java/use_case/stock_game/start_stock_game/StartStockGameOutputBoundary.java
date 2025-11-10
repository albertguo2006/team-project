package use_case.stock_game.start_stock_game;

/**
 * The output boundary for the START stock game Use Case.
 */
public interface StartStockGameOutputBoundary {
    /**
     * Prepares the success view for the stock game Use Case.
     * for when the amount the player invests is right
     * @param outputData the output data
     */
    void prepareSuccessView(StartStockGameOutputData outputData);

    /**
     * Prepares the failure view for START stock game Use Case.
     * for when the amount the player invests is invalid
     * meaning it is not 0 < x <= player's balance
     * OR the player has already played today
     * ** need to read from saved file..?
     * //TODO: figure out how to check if the player has already played the stock game today
     *
     * @param errorMessage the explanation of the failure
     *                     (invalid investing amount OR already played)
     */
    void prepareFailView(String errorMessage);

}