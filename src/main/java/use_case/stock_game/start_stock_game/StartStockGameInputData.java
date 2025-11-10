package use_case.stock_game.start_stock_game;

/**
 * input data for stock game use case
**/

public class StartStockGameInputData {
    private final Double startAmount;
    // starting amount that player is investing
    private final String date; // current date for user
    // TODO: might just get date automatically =, instead of relying on input?
    private final int gameDay; // current day (out of 5) that the player is on


    // initialise attributes
    public StartStockGameInputData(Double startAmount, String date, int gameDay) {
        this.startAmount = startAmount;
        this.date = date;
        this.gameDay = gameDay;
    }

    public Double getStartAmount() {
        return startAmount;
    }
    public String getDate() {
        return date;
    }
    public int getGameDay() {
        return gameDay;
    }
}
