package use_case.load_progress;

import entity.GameMap;
import entity.Player;

import java.io.IOException;

/**
 * DAO interface for the Load Progress Use Case.
 */

public interface LoadProgressDataAccessInterface {
    Player load(GameMap gameMap) throws IOException;
}
