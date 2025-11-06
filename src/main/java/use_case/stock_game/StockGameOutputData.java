package use_case.stock_game;

/**
 * output data for stock game use case
 **/

public class StockGameOutputData {

    private Double stockPrice;
    private Double shareNumber;
    private Double totalEquity;

    // initialise
    public StockGameOutputData(Double stockPrice, Double shareNumber, Double totalEquity) {
        this.stockPrice = stockPrice;
        this.shareNumber = shareNumber;
        this.totalEquity = totalEquity;
    }

    public Double getStockPrice() {
        return stockPrice;
    }

    public Double getShareNumber() {
        return shareNumber;
    }

    public Double getTotalEquity() {
        return totalEquity;
    }

}
