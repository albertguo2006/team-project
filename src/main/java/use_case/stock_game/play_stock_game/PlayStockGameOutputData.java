package use_case.stock_game.play_stock_game;

import java.util.List;

/**
 * output data for PLAY stock game use case
 **/

public class PlayStockGameOutputData {

    // player is shown the stock price (one at a time), and their total equity
    // (whether that comes from the straight cash,
    // or the equity of the shares they have at the current price)
    private List<Double> stockPriceHistory;
    // TODO: figure out later how to make this show one stock price at a time
    // either change here or in the interactor????? idk

    private Double totalEquity;
    // TODO: the total equity initially starts off as the startAmount from the startStockGame use case

    // initialise
    public PlayStockGameOutputData(List<Double> stockPriceHistory, Double totalEquity) {
        this.stockPriceHistory = stockPriceHistory;
        this.totalEquity = totalEquity;
    }

    public List<Double> getStockPriceHistory() {
        return stockPriceHistory;
    }

    public Double getTotalEquity() {
        return totalEquity;
    }

}
