package entity;

// this is a class to represent the stockHistory,
// which will store info about the stock including the name, company
// and uploaded price history (which will undoubtedly get written over
// when the game day changes)

import java.util.List;

public class StockHistory {
    String ticketSymbol;
    String companyName;
    private List<Double> pastPrices;

    // also, if pastPrices is just a list, then it will be written over each "day"
    // do we want that? if not, may need to consider doing a Hash Map of some sort


    public StockHistory(String ticketSymbol, String companyName, List<Double> pastPrices) {
        // need to call another method to load the data?
        this.ticketSymbol = ticketSymbol;
        this.companyName = companyName;
        this.pastPrices = pastPrices;
    }
}
