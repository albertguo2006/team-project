package use_case.save_progress;

import entity.Player;

import java.io.IOException;

public class SaveProgressInteractor {

    SaveProgressDataAccessInterface saveProgressDataAccessObject;

    public SaveProgressInteractor(SaveProgressDataAccessInterface saveProgressDataAccessObject) {
        this.saveProgressDataAccessObject = saveProgressDataAccessObject;
    }

    public void saveGame(Player player, String currentZone, String SAVE_FILE) throws IOException {
        saveProgressDataAccessObject.save(player, currentZone, SAVE_FILE);
    }
}



