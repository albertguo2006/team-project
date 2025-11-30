package use_case.save_progress;

import entity.Player;
import use_case.load_progress.LoadProgressOutputData;

import java.io.IOException;

public class SaveProgressInteractor implements SaveProgressInputBoundary{

    SaveProgressDataAccessInterface saveProgressDataAccessObject;
    SaveProgressOutputBoundary saveProgressPresenter;

    public SaveProgressInteractor(SaveProgressDataAccessInterface saveProgressDataAccessObject,
                                  SaveProgressOutputBoundary saveProgressPresenter) {
        this.saveProgressDataAccessObject = saveProgressDataAccessObject;
        this.saveProgressPresenter = saveProgressPresenter;
    }

    @Override
    public void saveGame(SaveProgressInputData saveProgressInputData) throws IOException {
        try {
            saveProgressDataAccessObject.save(saveProgressInputData.player, saveProgressInputData.getCurrentZone(),
                    saveProgressInputData.getFileName());
            SaveProgressOutputData saveProgressOutputData = new SaveProgressOutputData(
                    saveProgressInputData.player.getName(),
                    saveProgressInputData.player.getCurrentDay().getDisplayName());
            saveProgressPresenter.prepareSuccessView(saveProgressOutputData);
        }
        catch (IOException e) {
            throw e;
        }
    }
}
