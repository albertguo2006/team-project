package use_case.stock_game.play_stock_game;

import api.AlphaStockDataAccessObject;
import view.StockGameView;

// testing/running the game to see that it works
public class StockGameMain {

    public static void main(String[] args) {

        // make DAO
        PlayStockGameDataAccessInterface dataAccess = new AlphaStockDataAccessObject();

        // make presenter view
        PlayStockGameOutputBoundary presenter = new StockGameView();

        // make use case interactor (with the DAO and presenter)
        PlayStockGameInputBoundary interactor = new PlayStockGameInteractor(dataAccess, presenter);

        // testing input data to see if it works
        PlayStockGameInputData request = new PlayStockGameInputData(
                "VOO",
                10000.0,
                5
        );

        // run the game
        interactor.execute(request);
    }
}
