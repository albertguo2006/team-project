package use_case.stock_game.start_stock_game;

import entity.Player;

/**
 * input data for stock game use case
**/

public class StartStockGameInputData {
    private Double startAmount;
    // starting amount that player is investing
    private final Player player;

    // initialise attributes
    public StartStockGameInputData(Double startAmount, Player player) {
        this.startAmount = startAmount;
        this.player = player;
    }

    public Double getStartAmount() {
        return startAmount;
    }
    public Player getPlayer() {return player;}
}
