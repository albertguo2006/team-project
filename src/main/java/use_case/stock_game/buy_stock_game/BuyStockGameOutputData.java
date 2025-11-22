package use_case.stock_game.buy_stock_game;

import java.util.List;

/**
 * output data for PLAY stock game use case
 **/

public class BuyStockGameOutputData {
    private Double cash = 0.0;
    private Double shares;

    // initialise
    public BuyStockGameOutputData(Double cash, Double shares) {
        this.cash = cash;
        this.shares = shares;
    }

    public Double getShares() {
        return shares;
    }

}
