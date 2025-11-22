package use_case.stock_game.play_stock_game;

import entity.Portfolio;
import entity.Stock;

import javax.swing.*;
import java.util.List;
import java.util.Random;

/**
 * The PLAY stock game use case interactor.
 */

public class PlayStockGameInteractor implements PlayStockGameInputBoundary {

    private final PlayStockGameDataAccessInterface dataAccess;
    private final PlayStockGameOutputBoundary presenter;

    // initialise
    public PlayStockGameInteractor(PlayStockGameDataAccessInterface dataAccess,
                                   PlayStockGameOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    // TODO: subtract start amount from player balance, and then add total equity once done playing

    /**
     * play the game! until it ends
     * @param inputData
     */
    @Override
    public void execute(PlayStockGameInputData inputData) {

        try {   // get the list of prices for that symbol and the corresponding day
            List<Double> realPrices = dataAccess.getIntradayPrices(inputData.symbol, inputData.days);

            Portfolio portfolio = new Portfolio(); // make new portfolio
            double startingPrice = realPrices.get(0); // get first stock price

            Stock stock = new Stock(inputData.symbol, startingPrice); // make Stock with that info
            portfolio.loadStock(stock); // add stock to portfolio
            portfolio.setCash(inputData.startAmount); // set portfolio cash to starting amount (at the very start)

            double[] lastPrice = {startingPrice};
            int[] ticks = {0}; // counter to eevntaulyl end game

            Timer timer = new Timer(250, e -> {
                ticks[0]++;

                // check to make sure it is not game over yet
                if (ticks[0] >= realPrices.size() || portfolio.getTotalEquity() <= 0) {
                    ((Timer) e.getSource()).stop();

                    // present game-over view
                    presenter.presentGameOver(new PlayStockGameOutputData(portfolio.getCash(),portfolio.getShares(stock),
                                    portfolio.getTotalEquity(),lastPrice[0]));
                    return;
                }

                // move to next price in list of stock prices
                realPrices.add(realPrices.remove(0));
                double nextRealPrice = realPrices.get(0);
                double diff = nextRealPrice - lastPrice[0];

                Random random = new Random(); // make random object to use in algorithm for next price
                double momentum = diff * 0.3;
                double noise = random.nextGaussian() * 0.01 * nextRealPrice;

                double price = nextRealPrice + diff * 0.04 + momentum + noise;

                lastPrice[0] = price;
                // update view with new price
                presenter.presentPriceUpdate(new PlayStockGameOutputData(portfolio.getCash(),portfolio.getShares(stock),
                                portfolio.getTotalEquity(),price));
            });

            // present start game view
            presenter.presentGameStart(portfolio, stock, timer);

            // if error, present error message
        } catch (Exception ex) {
            presenter.presentError(ex.getMessage());
        }
    }
}