package interface_adapter.save_progress;

import entity.Player;
import use_case.save_progress.SaveProgressInputBoundary;
import use_case.save_progress.SaveProgressInputData;

import java.io.IOException;

public class SaveProgressController {
    SaveProgressInputBoundary saveProgressInteractor;
    public SaveProgressController(SaveProgressInputBoundary saveProgressInteractor) {
        this.saveProgressInteractor = saveProgressInteractor;
    }

    public void saveGame(Player player, String filePath, String currentZone) throws IOException {
        SaveProgressInputData saveProgressInputData = new SaveProgressInputData(player, filePath, currentZone);
        saveProgressInteractor.saveGame(saveProgressInputData);
    }
}
