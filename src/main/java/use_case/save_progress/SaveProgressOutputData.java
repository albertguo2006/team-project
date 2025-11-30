package use_case.save_progress;

public class SaveProgressOutputData {
    String playerName;
    String currentDay;

    public SaveProgressOutputData(String playerName, String currentDay) {
        this.playerName = playerName;
        this.currentDay = currentDay;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getCurrentDay() {
        return currentDay;
    }
}

