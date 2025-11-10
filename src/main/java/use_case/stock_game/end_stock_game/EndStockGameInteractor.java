package use_case.stock_game.end_stock_game;

import entity.Stock;

import java.util.List;

/**
 * The stock game Interactor.
 */
public class EndStockGameInteractor implements EndStockGameInputBoundary {

    private final EndStockGameDataAccessInterface stockGameDataAccessObject;
    private final EndStockGameOutputBoundary loginPresenter;

    public EndStockGameInteractor(EndStockGameDataAccessInterface endStockGameDataAccessInterface,
                                  EndStockGameOutputBoundary endStockGameOutputBoundary){
        this.stockGameDataAccessObject = endStockGameDataAccessInterface;
        this.loginPresenter = endStockGameOutputBoundary;
    }

    public void execute(EndStockGameInputData endStockGameInputData){

    }



    // TODO: these methods below need to go somewhere else but i dont know where so thats a later problem
    /**
     * Returns the list of stock prices given the stock, date, gameday.
     * @param stock which stock you want data on
     * @param date (String?) for the date of the data
     * @param gameday integer from 1-5 for which day of the game thy're on
     * @return stockPastPrices which are that stock's prices for that day
     **/
    //List<Double> getStockPastPrices(Stock stock, String date, int gameday){

   // };


    //TODO
    /**
     * Returns the number of stock shares bought given the stock price and cash amount.
     * @param //
     * @return //
     */


    //TODO
    /**
     * Returns the total equity given the current stock shares, stock prices AND cash.
     * @param //
     * @return //
     */

    //TODO
    /**
     * Returns the NET profit/loss given the current stock shares and stock prices.
     * @param //
     * @return //
     */

    //TODO
    /**
     * Adjusts player cash and stock share numbers so that player "buys" shares
     * @param //
     */

    //TODO
    /**
     * Adjusts player cash and stock share numbers so that player "sells" shares
     * @param //
     */

    //TODO
    /**
     * End game, by selling all stock share and going to end screen
     * @param //
     */


}
