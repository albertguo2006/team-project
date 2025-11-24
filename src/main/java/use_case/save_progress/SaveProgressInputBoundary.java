package use_case.save_progress;

import java.io.IOException;

public interface SaveProgressInputBoundary {
    void saveGame(SaveProgressInputData saveProgressInputData) throws IOException;
}
