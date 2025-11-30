package use_case.stock_game;

/**
 * output data for PLAY stock game use case
 **/

public class PlayStockGameOutputData {

    public final double cash; // cash the player has
    public final double shares; // number of shares
    public final double totalEquity; // total equity currently from the shares and cash
    public final double price; // price of the stock at the moment

    // intialise the variables
    public PlayStockGameOutputData(double cash, double shares, double totalEquity, double price) {
        this.cash = cash;
        this.shares = shares;
        this.totalEquity = totalEquity;
        this.price = price;
    }

}
