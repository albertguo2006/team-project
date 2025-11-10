package use_case.stock_game.start_stock_game;

import java.util.List;

/**
 * output data for START stock game use case
 **/

public class StartStockGameOutputData {

    private Double startAmount;

    public StartStockGameOutputData(Double startAmount) {
        this.startAmount = startAmount;
    }
    public Double getStartAmount() {
        return startAmount;
    }

}
