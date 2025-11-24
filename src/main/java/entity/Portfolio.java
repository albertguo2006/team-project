package entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// this is a class to represent the portfolio of the playet
// current only has one type of stock, but could be altered to add more?
public class Portfolio {

    public double cash;   // starting cash
    public double totalEquity;  // total equity field
    public HashMap<Stock, Double> investments = new HashMap<>();
    // investments is a hashmap from the stock (which includes symbol and price)
    // to the number of shares they have of that stock (open for extension)


    public Portfolio() {}

    public Portfolio(Double totalEquity, HashMap<Stock, Double> investments) {
        this.totalEquity = totalEquity;
        this.investments = investments;
    }

    /**
     * GET the total equity (total money).
     */
    // TODO: should this be in the portfolio class or somewhere else?
    //  need to load the api data into the stock class

    public void loadStock(Stock stock){
        // add to the investments portfolio, with currently 0.0 shares bought
        investments.put(stock, 0.0);

    }

    public Double getTotalEquity() {
        Double equity = 0.0; // initialise equity
        for (Stock stock: investments.keySet()) { // for each stock
            equity +=  investments.get(stock)*stock.stockPrice;
            // multiple number of shares by price per share
        }
        return cash + equity;  // sum up and return total
    }

    public void buy(Stock stock) {
        investments.put(stock, cash/stock.stockPrice);
        // add the number of shares bought in the investments dict
        cash = 0; // no more cash (use all of it to buy)
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

    public Map<Stock, Double> getInvestments() {
        return investments;
    }

    public void  setInvestments(Map<Stock, Double> investments) {
        this.investments = (HashMap<Stock, Double>) investments;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Portfolio)){
            return false;
        }
        Portfolio portfolio = (Portfolio) obj;
        if (totalEquity != portfolio.totalEquity) {
            return false;
        }
        if (investments.size() != portfolio.investments.size()) {
            return false;
        }
        Map <Stock, Double> other_investments = portfolio.getInvestments();
        for (Stock stock: investments.keySet()) {
            if (!other_investments.containsKey(stock)) {
                return false;
            }
            if (!investments.get(stock).equals(other_investments.get(stock))) {
                return false;
            }
        }
        return true;
    }

}

