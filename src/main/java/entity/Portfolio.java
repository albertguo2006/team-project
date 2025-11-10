package entity;

import java.util.HashMap;

// this is a class to represent the portfolio of the playet
// current only has one type of stock, but could be altered to add more?
public class Portfolio {

    private Double totalEquity = 0.0; // default is 0
    private HashMap<Stock, Double> investments;
    // investments is a hashmap from the stock (which includes symbol and price)
    // to the number of shares they have of that stock

    /**
     * Set the total equity (total money) from the stock shares, prices and cash.
     */
    public void setTotalEquity() {
        // loop through each stock

        double total = 0.0;
        for (Stock s: investments.keySet()){
            total += s.stockPrice * investments.get(s);
        }
        this.totalEquity = total;
    }

    /**
     * GET the total equity (total money).
     */
    public Double getTotalEquity() {
        return totalEquity;
    }

}
