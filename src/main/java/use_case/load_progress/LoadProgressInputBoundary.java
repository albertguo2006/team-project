package use_case.load_progress;

import entity.Player;

import java.io.IOException;

public interface LoadProgressInputBoundary {
    Player loadGame(LoadProgressInputData loadProgressInputData) throws IOException;
}
