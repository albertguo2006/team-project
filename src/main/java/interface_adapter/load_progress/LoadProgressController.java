package interface_adapter.load_progress;

import entity.GameMap;
import entity.Player;
import use_case.load_progress.LoadProgressInputBoundary;
import use_case.load_progress.LoadProgressInputData;

import java.io.IOException;

public class LoadProgressController {
    LoadProgressInputBoundary loadProgressInteractor;
    public LoadProgressController(LoadProgressInputBoundary loadProgressInteractor) {
        this.loadProgressInteractor = loadProgressInteractor;
    }

    public Player loadGame(GameMap gameMap,String filePath) throws IOException {
        LoadProgressInputData loadProgressInputData = new LoadProgressInputData(gameMap, filePath);
        return loadProgressInteractor.loadGame(loadProgressInputData);
    }
}
