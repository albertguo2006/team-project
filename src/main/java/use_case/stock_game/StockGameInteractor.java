package use_case.stock_game;

/**
 * The stock game Interactor.
 */
//TODO eventually break this up into multiple interactors?
// single responsibility or something
public class StockGameInteractor {

    private final StockGameDataAccessInterface stockGameDataAccessObject;
    private final StockGameOutputBoundary loginPresenter;

    public StockGameInteractor(StockGameDataAccessInterface stockGameDataAccessInterface,
                               StockGameOutputBoundary stockGameOutputBoundary){
        this.stockGameDataAccessObject = stockGameDataAccessInterface;
        this.loginPresenter = stockGameOutputBoundary;
    }

    /**
     * Returns the list of stock prices given the date, gameday.
     * @param //
     * @return//
     */
    User get(String username);


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
