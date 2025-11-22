package use_case.stock_game.end_stock_game;

import entity.Stock;

import java.util.List;

/**
 * The END stock game Interactor.
 */
public class EndStockGameInteractor implements EndStockGameInputBoundary {

    private final EndStockGameDataAccessInterface stockGameDataAccessObject;
    private final EndStockGameOutputBoundary stockGamePresenter;

    public EndStockGameInteractor(EndStockGameDataAccessInterface endStockGameDataAccessInterface,
                                  EndStockGameOutputBoundary stockGamePresenter) {
        this.stockGameDataAccessObject = endStockGameDataAccessInterface;
        this.stockGamePresenter = stockGamePresenter;
    }

    //TODO
    /**
     * End game, by selling all stock share and going to end screen
     * @param //
     */

    //TODO
    /**
     * Returns the NET profit/loss given the current stock shares and stock prices.
     * @param //
     * @return //
     */


    public void execute() {
        // Implement the end/leave game logic:
        // 1. remember the total equity/ earnings
        final Double totalEquity = this.stockGameDataAccessObject.getTotalEquity();

        // 2. create output data and notify the presenter
        final EndStockGameOutputData outputData = new EndStockGameOutputData(totalEquity);
        this.stockGamePresenter.prepareSuccessView(outputData);
    }
}