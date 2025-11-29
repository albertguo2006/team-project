package interface_adapter.load_progress;

import interface_adapter.ViewModel;

import java.util.Random;

public class LoadProgressViewModel extends ViewModel {
    String playerName;

    private String[] loadingMessages = {
            "Generating Global Recession...",
            "Ready to work at your 9-5?",
            "Can you survive 5 days without going bankrupt?",
            "Don't forget to pay your bills!",
            "Welcome back! Sir Maximilian Alexander Percival Ignatius Thaddeus Montgomery-Worthington III missed you!",
            "Certain Items can give you benefits! Go to Walmart to check it out!"
    };

    public LoadProgressViewModel() {
        super("Loading Game");
    }

    public String getloadingMessage(){
        Random random = new Random();
        return loadingMessages[random.nextInt(loadingMessages.length)];
    }

    public void setPlayerName(String playerName) {
       this.playerName = playerName;
    }

}
