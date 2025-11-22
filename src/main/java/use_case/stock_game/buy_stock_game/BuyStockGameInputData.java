package use_case.stock_game.buy_stock_game;

import entity.Player;

/**
 * input data for START stock game use case
**/

public class BuyStockGameInputData {
    private Double cash;
    private Double price;

    // initialise attributes
    public BuyStockGameInputData(Double cash, Double price) {
        this.cash = cash;
        this.price = price;
    }

    public Double getCash() {
        return cash;
    }
    public Double getPrice() {return price;}
}
