package data_access;

import entity.Day;
import entity.DaySummary;
import entity.Player;
import use_case.sleep.SleepDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

public class InMemorySleepDataAccessObject implements SleepDataAccessInterface {
    private Player savedPlayer;
    private List<DaySummary> createdSummaries = new ArrayList<>();


    @Override
    public void savePlayerState(Player player) {
        this.savedPlayer = player;
    }

    @Override
    public DaySummary createDaySummary(Day day, double earnings, double spending, double newBalance) {
        DaySummary summary = new DaySummary(day, earnings, spending, newBalance);
        createdSummaries.add(summary);
        return summary;
    }

    @Override
    public DaySummary createDaySummary(Day day, double earnings, double spending, double stockProfitLoss, double newBalance) {
        DaySummary summary = new DaySummary(day, earnings, spending, stockProfitLoss, newBalance);
        createdSummaries.add(summary);
        return summary;
    }

    // Test helper methods
    public Player getSavedPlayer() {
        return savedPlayer;
    }

    public List<DaySummary> getCreatedSummaries() {
        return new  ArrayList<>(createdSummaries);
    }

    public void clear(){
        savedPlayer = null;
        createdSummaries.clear();
    }
}
