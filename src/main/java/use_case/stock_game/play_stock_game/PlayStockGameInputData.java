package use_case.stock_game.play_stock_game;

/**
 * input data for START stock game use case
**/

public class PlayStockGameInputData {
    public final String symbol;
    public final double startAmount; // initial investing amount
    public final int days;

    public PlayStockGameInputData(String symbol, double startAmount, int day) {
        this.symbol = symbol;
        this.startAmount = startAmount;
        this.days = day;
    }
}
