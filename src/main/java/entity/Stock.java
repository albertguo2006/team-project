package entity;

// this is a class to represent the stock, which will store info about the stock including the name
// and CURRENT (in-game) price histories
public class Stock {
    String ticketSymbol;
    String companyName;
    Double stockPrice;
    // stock price gets constantly reupdated to the next values in the priceHistory

    public Stock(String ticketSymbol, String companyName, Double stockPrice) {
        // need to call another method to load the data? load data from Stock history
        this.ticketSymbol = ticketSymbol;
        this.companyName = companyName;
        this.stockPrice = stockPrice;
    }

}
