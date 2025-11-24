package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents metadata about a stock including risk level and data availability.
 */
public class StockInfo {
    private String symbol;
    private String name;
    private String riskLevel;  // LOW, MEDIUM, HIGH
    private double volatility;
    private List<String> availableMonths;
    private List<String> playedPeriods;

    public StockInfo() {
        this.availableMonths = new ArrayList<>();
        this.playedPeriods = new ArrayList<>();
    }

    public StockInfo(String symbol, String name, String riskLevel) {
        this.symbol = symbol;
        this.name = name;
        this.riskLevel = riskLevel;
        this.volatility = 0.0;
        this.availableMonths = new ArrayList<>();
        this.playedPeriods = new ArrayList<>();
    }

    // Getters and setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public List<String> getAvailableMonths() {
        return availableMonths;
    }

    public void setAvailableMonths(List<String> availableMonths) {
        this.availableMonths = availableMonths;
    }

    public List<String> getPlayedPeriods() {
        return playedPeriods;
    }

    public void setPlayedPeriods(List<String> playedPeriods) {
        this.playedPeriods = playedPeriods;
    }

    /**
     * Adds a month to the list of available months if not already present.
     */
    public void addAvailableMonth(String month) {
        if (!availableMonths.contains(month)) {
            availableMonths.add(month);
        }
    }

    /**
     * Adds a played period to the list.
     */
    public void addPlayedPeriod(String period) {
        if (!playedPeriods.contains(period)) {
            playedPeriods.add(period);
        }
    }

    /**
     * Checks if this stock has unplayed data available.
     */
    public boolean hasUnplayedData() {
        // If no data has been fetched yet, return false
        if (availableMonths.isEmpty()) {
            return false;
        }

        // Check if we have unplayed periods
        // For simplicity, we'll estimate: each month has ~4 playable 5-day periods
        int estimatedPeriods = availableMonths.size() * 4;
        return playedPeriods.size() < estimatedPeriods;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", symbol, name, riskLevel);
    }
}
