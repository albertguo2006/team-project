package use_case.stock_game.end_stock_game;

/**
 * output data for END stock game use case
 **/

public class EndStockGameOutputData {

    private Double totalEquity;
    // basically total money (whether from cash or stocks)

    // initialise
    public EndStockGameOutputData(Double totalEquity) {
        this.totalEquity = totalEquity;
    }

    public Double getTotalEquity() {
        return totalEquity;
    }

}
