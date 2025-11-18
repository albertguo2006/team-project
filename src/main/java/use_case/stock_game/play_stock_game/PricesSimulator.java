package use_case.stock_game.play_stock_game;

import java.util.List;

public class PricesSimulator {

    private final List<Double> realPrices;
    private int realIndex = 0;

    private double currentPrice;
    private double targetPrice;

    public PriceSimulator(List<Double> realPrices) {
        this.realPrices = realPrices;
        this.currentPrice = realPrices.get(0);
        this.targetPrice = realPrices.get(1);
    }

    // Called once per second
    public double nextPrice() {
        // Step to next real point if close enough
        if (Math.abs(currentPrice - targetPrice) < 0.05 && realIndex < realPrices.size() - 1) {
            realIndex++;
            targetPrice = realPrices.get(realIndex);
        }

        // random noise
        double noise = (Math.random() - 0.5) * 0.3;

        // drift toward target
        double drift = (targetPrice - currentPrice) * 0.02;

        currentPrice += noise + drift;

        return currentPrice;
    }
}
