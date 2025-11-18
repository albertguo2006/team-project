package use_case.load_progress;

import entity.Player;

import java.io.IOException;

/**
 * DAO interface for the Load Progress Use Case.
 */

public interface LoadProgressDataAccessInterface {
    Player load() throws IOException;
}
