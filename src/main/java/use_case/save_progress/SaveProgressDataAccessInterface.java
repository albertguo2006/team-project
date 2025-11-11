package use_case.save_progress;

import entity.Player;

import java.io.IOException;

/**
 * DAO interface for the Save Progress Use Case.
 */
public interface SaveProgressDataAccessInterface {
    void save(Player player) throws IOException;
}
