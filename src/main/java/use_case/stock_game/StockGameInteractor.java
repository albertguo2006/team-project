package use_case.stock_game;

/**
 * The stock game Interactor.
 */

public class StockGameInteractor {

    private final StockGameDataAccessInterface stockGameDataAccessObject;
    private final StockGameOutputBoundary loginPresenter;

    public StockGameInteractor(StockGameDataAccessInterface stockGameDataAccessInterface,
                               StockGameOutputBoundary stockGameOutputBoundary){
        this.stockGameDataAccessObject = stockGameDataAccessInterface;
        this.loginPresenter = stockGameOutputBoundary;
    }

}
