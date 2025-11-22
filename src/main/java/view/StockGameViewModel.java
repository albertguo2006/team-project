package view;

import use_case.stock_game.play_stock_game.PlayStockGameOutputData;

public class StockGameViewModel {

    public double price;
    public double cash;
    public double shares;
    public double equity;

    public double lastPrice; // to calculate next price and present green/red arrow

    public void update(PlayStockGameOutputData data) {
        this.price = data.price;
        this.cash = data.cash;
        this.shares = data.shares;
        this.equity = data.totalEquity;
    }
}