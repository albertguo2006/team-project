package use_case.stock_game.play_stock_game;

import java.util.List;

/*
* PriceSimulator, which uses the api data and extrapolates more data points
* to be used in the stock game
 */

public class PriceSimulator {

    private final List<Double> realPrices;
    private int realIndex = 0;
    private double currentPrice;
    private double targetPrice;

    public PriceSimulator(List<Double> realPrices) {
        this.realPrices = realPrices;
        this.currentPrice = realPrices.get(0);
        this.targetPrice = realPrices.get(1);
    }

    // get next price once every second
    public double nextPrice() {
        // if there is a small jump in the data, then can just use the next data point
        if (Math.abs(currentPrice - targetPrice) < 0.05 && realIndex < realPrices.size() - 1) {
            realIndex++;
            targetPrice = realPrices.get(realIndex);
        }

        else{       // else, add noise to randomise (somewhat) the stock prices
            double noise = (Math.random() - 0.5) * 0.3;
            // calculate difference between the two prices,
            // (sorta like gradient decent process)
            double drift = (targetPrice - currentPrice) * 0.02;
            currentPrice += noise + drift;      // calculate the next price
        }

        return currentPrice;
    }
}
