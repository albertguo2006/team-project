package entity;

import java.util.List;

// this is a class to represent the stock, which will store info about the stock including the name
// and uploaded price histories
public class Stock {
    String ticketSymbol;
    String companyName;
    private List<Double> priceHistory;

    // note that I still need to iron out the details of this,
    // not sure how this will be, since prices may need to be retreieved and stored
    // AS the player clicks play, instead of before, since the number of data points
    // will depend on if the player has drank caffiene or not etc.
    // also, if priceHistory is just a list, then it will be written over each "day"
    // do we want that? if not, may need to consider doing a Hash Map of some sort


    public Stock(String ticketSymbol, String companyName, List<Double> priceHistory) {
        // need to call another method to load the data?
        this.ticketSymbol = ticketSymbol;
        this.companyName = companyName;
        this.priceHistory = priceHistory;
    }
}
