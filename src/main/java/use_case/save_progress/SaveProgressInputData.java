package use_case.save_progress;
import entity.Player;

public class SaveProgressInputData {
    Player player;
    private final String fileName;
    String currentZone;

    public SaveProgressInputData(Player player, String filePath, String currentZone) {
        this.player = player;
        this.fileName = filePath;
        this.currentZone = currentZone;
    }

    public Player getPlayer() {
        return player;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCurrentZone() {
        return currentZone;
    }

}
