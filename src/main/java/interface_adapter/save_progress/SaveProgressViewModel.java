package interface_adapter.save_progress;

import interface_adapter.ViewModel;

import java.time.LocalDate;
import java.util.Random;

public class SaveProgressViewModel extends ViewModel {
    private String playerName;
    private String currentDay;
    private String saveDate;

    private String[] savingMessages = {
            "Don't quit the game! Currently saving your recent progress...",
            "Keeping track of all your unpaid bills...",
            "Monitoring and saving your performance at work...",
            "Don't forget to save your data everytime you make significant changes!",
            "Keeping track of all the special items in your inventory!"
    };

    public SaveProgressViewModel() {
        super("Save Progress");
    }

    public String getSavingMessage(){
        Random random = new Random();
        return savingMessages[random.nextInt(savingMessages.length)];
    }

    // Saves player's name, the in-game day of their save, and the date of their last save.
    public void setData(String playerName, String currentDay) {
        this.playerName = playerName;
        this.currentDay = currentDay;
        this.saveDate = LocalDate.now().toString();
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public String getSaveDate() {
        return saveDate;
    }
}
