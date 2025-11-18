package entity;

import java.util.HashMap;

// this is a class to represent the portfolio of the playet
// current only has one type of stock, but could be altered to add more?
public class Portfolio {

    public double cash;   // starting cash
    public HashMap<Stock, Double> investments;
    // investments is a hashmap from the stock (which includes symbol and price)
    // to the number of shares they have of that stock (open for extension)


    /**
     * GET the total equity (total money).
     */
    // TODO: should this be in the portfolio class or somewhere else?
    //  need to load the api data into the stock class

    public Double getTotalEquity() {
        Double equity = 0.0; // initialise equity
        for (Stock stock: investments.keySet()) { // for each stock
            equity +=  investments.get(stock)*stock.stockPrice;
            // multiple number of shares by price per share
        }
        return cash + equity;  // sum up and return total
    }

    public void buy(Stock stock) {
        cash = 0; // no more cash (use all of it to buy)
        investments.put(stock, cash/stock.stockPrice);
        // add the number of shares bought in the investments dict
    }

    public void sell(Stock stock) {
        cash = investments.get(stock)*stock.stockPrice;
        // sell all shares, so cash will be number of shares * share price
        investments.put(stock, 0.0);
        // then re-initialise number of shares to be 0.0 (sold all of them)
    }

    public Double getShares(Stock stock) {
        return investments.getOrDefault(stock, 0.0);
        // either return the share number, or 0.0 if the stock is not in investments dict
    }

    public Double getCash() {
        return cash;
    }

    public void  setCash(double cash) {
        this.cash = cash;
    }

    public HashMap<Stock, Double> getInvestments() {
        return investments;
    }

    public void  setInvestments(HashMap<Stock, Double> investments) {
        this.investments = investments;
    }
}

