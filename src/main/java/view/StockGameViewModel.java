package view;

import use_case.stock_game.PlayStockGameOutputData;

import java.util.LinkedList;
import java.util.Queue;

public class StockGameViewModel {

    public static final int MAX_HISTORY_SIZE = 50;

    public double price;
    public double cash;
    public double shares;
    public double equity;

    public double lastPrice; // to calculate next price and present green/red arrow

    public Queue<Double> priceHistory = new LinkedList<>();

    public void update(PlayStockGameOutputData data) {
        this.price = data.price;
        this.cash = data.cash;
        this.shares = data.shares;
        this.equity = data.totalEquity;
    }

    public void addPriceToHistory(double price) {
        priceHistory.add(price);
        if (priceHistory.size() > MAX_HISTORY_SIZE) {
            priceHistory.poll(); // Remove oldest price
        }
    }
}