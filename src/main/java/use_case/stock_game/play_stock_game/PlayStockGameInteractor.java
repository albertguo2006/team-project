package use_case.stock_game.play_stock_game;

import entity.Stock;

import java.util.List;

//TODO: need to seperate into buy and sell stock use cases...?
// and then during end_stock game use case, maybe
// call the sell stock if they still have stocks?
// so that way they always cash out at the end???

/**
 * The PLAY stock game Interactor.
 */

public class PlayStockGameInteractor implements PlayStockGameInputBoundary {

    private final PlayStockGameDataAccessInterface stockGameDataAccessObject;
    private final PlayStockGameOutputBoundary stockGamePresenter;

    public PlayStockGameInteractor(PlayStockGameDataAccessInterface playStockGameDataAccessInterface,
                                   PlayStockGameOutputBoundary playStockGameOutputBoundary){
        this.stockGameDataAccessObject = playStockGameDataAccessInterface;
        this.stockGamePresenter = playStockGameOutputBoundary;
    }

    public void execute(){
        // need input which is the startAmount
        // TODO: ???? does this^ go into a inputdata object even though its not from the user (should be internal)


    }




    // TODO: these methods below need to go somewhere else but i dont know where so thats a later problem
    /**
     * Returns the list of stock prices given the stock, date, gameday.
     * @param stock which stock you want data on
     * @param date (String?) for the date of the data
     * @param gameday integer from 1-5 for which day of the game thy're on
     * @return stockPastPrices which are that stock's prices for that day
     */
    //List<Double> getStockPastPrices(Stock stock, String date, int gameday);


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
     * Adjusts player cash and stock share numbers so that player "buys" shares
     * @param //
     */

    //TODO
    /**
     * Adjusts player cash and stock share numbers so that player "sells" shares
     * @param //
     */



}
