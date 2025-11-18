package use_case.load_progress;

import entity.GameMap;
import entity.Player;

import java.io.IOException;

public class LoadProgressInteractor {
    LoadProgressDataAccessInterface loadProgressDataAccessObject;

    public LoadProgressInteractor(LoadProgressDataAccessInterface loadProgressDataAccessObject) {
        this.loadProgressDataAccessObject = loadProgressDataAccessObject;
    }

    public Player loadGame(GameMap gameMap) throws IOException {
       return loadProgressDataAccessObject.load(gameMap);
    }

}
