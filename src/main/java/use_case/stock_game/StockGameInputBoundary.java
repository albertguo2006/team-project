package use_case.stock_gane;

/**
 * Input Boundary for actions related to the stock game.
 */
public interface LoginInputBoundary {

    /**
     * Executes the stock game use case.
     * @param stockInputData the input data
     */
    void execute(StockInputData stockInputData);
}
