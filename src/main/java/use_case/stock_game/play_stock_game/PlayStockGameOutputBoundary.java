package use_case.stock_game.play_stock_game;

import entity.Portfolio;
import entity.Stock;

import javax.swing.Timer;

/**
 * output boundary for the PLAY stock game Use Case.
 * (preparing the views)
 */
public interface PlayStockGameOutputBoundary {
    // prepare the various views, for when the game starts,
    // when the price is updated (during the game)
    // and when the game ends
    // as well as when there is an error
    void presentGameStart(Portfolio portfolio, Stock stock, Timer timer);
    void presentPriceUpdate(PlayStockGameOutputData data);
    void presentGameOver(PlayStockGameOutputData data);
    void presentError(String message);
    /**
     * Prepares the success view for the stock game Use Case.
     * for when the amount the player invests is right
     * @param outputData the output data
     */
    void prepareSuccessView(PlayStockGameOutputData outputData);

    /**
     * Prepares the failure view for the stock game Use Case.
     * for when the amount the player invests is invalid
     * meaning it is not 0<x<=savings
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

}