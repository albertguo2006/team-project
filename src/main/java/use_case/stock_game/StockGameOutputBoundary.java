package use_case.stock_game;

/**
 * The output boundary for the stock game Use Case.
 */
public interface StockGameOutputBoundary {
    /**
     * Prepares the success view for the stock game Use Case.
     * for when the amount the player invests is right
     * @param outputData the output data
     */
    void prepareSuccessView(StockOutputData outputData);

    /**
     * Prepares the failure view for the stock game Use Case.
     * for when the amount the player invests is invalid
     * meaning it is not 0<x<=savings
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

}