package use_case.stock_game.play_stock_game;

import entity.Player;

/**
 * input data for START stock game use case
**/

public class PlayStockGameInputData {
    private final Player player;
    private Double startAmount;

    // initialise attributes
    public PlayStockGameInputData(Double startAmount, Player player) {
        this.startAmount = startAmount;
        this.player = player;
    }

    public Double getStartAmount() {
        return startAmount;
    }
    public Player getPlayer() {return player;}
}
