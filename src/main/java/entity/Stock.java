package entity;

import java.util.Objects;

// this is a class to represent the stock, which will store info about the stock including the name
// and CURRENT (in-game) price histories
public class Stock {
    public String ticketSymbol;
    public Double stockPrice;
    // stock price gets constantly reupdated to the next values in the priceHistory

    public Stock(String ticketSymbol, Double stockPrice) {
        // need to call another method to load the data? load data from Stock history
        this.ticketSymbol = ticketSymbol;
        this.stockPrice = stockPrice;
    }

    // get and setter methods for the instance attributes
    public void setStockPrice(Double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public void setTicketSymbol(String ticketSymbol) {
        this.ticketSymbol = ticketSymbol;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public String getTicketSymbol() {
        return ticketSymbol;
    }

    // override the equals built in function, so that it will take care of
    // cases where the stocks have the same ticket symbol, therefore are the same
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Stock)) {
            return false;
        } else {
            Stock stock = (Stock) obj;
            return Objects.equals(ticketSymbol, stock.ticketSymbol);
        }
    }

    // again overriding the hashcode
    @Override
    public int hashCode() {
        return Objects.hash(ticketSymbol);
    }

    // overriding the to string to make debugging easier
    @Override
    public String toString() {
        return ticketSymbol + "@ $" + stockPrice;
    }
}
