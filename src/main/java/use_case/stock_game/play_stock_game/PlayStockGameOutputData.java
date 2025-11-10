package use_case.stock_game.play_stock_game;

import java.util.List;

/**
 * output data for stock game use case
 **/

public class PlayStockGameOutputData {

    private List<Double> stockPriceHistory;
    private Double shareNumber;
    private Double totalEquity;
    // TODO should this instead be portfolio?????

    // initialise
    public PlayStockGameOutputData(List<Double> stockPriceHistory, Double shareNumber, Double totalEquity) {
        this.stockPriceHistory = stockPriceHistory;
        this.shareNumber = shareNumber;
        this.totalEquity = totalEquity;
    }

    public List<Double> getStockPriceHistory() {
        return stockPriceHistory;
    }

    public Double getShareNumber() {
        return shareNumber;
    }

    public Double getTotalEquity() {
        return totalEquity;
    }

}
