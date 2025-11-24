package use_case.load_progress;

import entity.GameMap;

public class LoadProgressInputData {
    private final GameMap gameMap;
    private final String fileName;

    public LoadProgressInputData(GameMap gameMap, String filePath) {
        this.gameMap = gameMap;
        this.fileName = filePath;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public String getFileName() {
        return fileName;
    }
}
