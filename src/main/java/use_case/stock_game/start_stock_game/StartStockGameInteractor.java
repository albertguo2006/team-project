package use_case.stock_game.start_stock_game;

import api.AlphaStockDataBase;
import api.StockDataBase;
import entity.Player;
import entity.Stock;

import java.util.List;

import static api.AlphaStockDataBase.getIntradayOpensForGameDay;

/**
 * The START stock game Interactor.
 */
public class StartStockGameInteractor implements StartStockGameInputBoundary {

    private final StartStockGameDataAccessInterface stockGameDataAccessObject;
    private final StartStockGameOutputBoundary stockGamePresenter;
    private StockDataBase stockDataBase;

    public StartStockGameInteractor(StartStockGameDataAccessInterface stockGameDataAccessObject,
                                    StartStockGameOutputBoundary stockGamePresenter,
                                    StockDataBase stockDataBase) {
        this.stockGameDataAccessObject = stockGameDataAccessObject;
        this.stockGamePresenter = stockGamePresenter;
        this.stockDataBase = stockDataBase;

    }

    @Override
    public void execute(StartStockGameInputData startStockGameInputData) throws Exception {
        final Double startAmount = startStockGameInputData.getStartAmount();
        final Player player = startStockGameInputData.getPlayer();

        // check that the input starting amount is valid
        if (player.getBalance() < startAmount || startAmount <= 0) {
            stockGamePresenter.prepareFailView(startAmount + " is not a valid investment amount. " +
                    "\n Your investment amount must be greater than zero and less than your current balance.");
        }

        // save the json data from the api call to a file with the stock symbol as the filename
        this.stockDataBase.getStockPrices("VOO");


        // TODO: how to check if player has already "gone to work" (played stock game today), given player and game day?
        // else if (player.clockedIn(gameDay)) {
//           stockGamePresenter.prepareFailView("Sorry, you've already worked today! \n" +
//                   "come back tomorrow to clock back in again!");};
        //else {

            final StartStockGameOutputData stockOutputData = new StartStockGameOutputData(startAmount);

            stockGamePresenter.prepareSuccessView(stockOutputData);

            // call database methods
            // fetch the api data for the given game day
            AlphaStockDataBase.getIntradayOpensForGameDay("VOO", 2);

        // }
    }

}