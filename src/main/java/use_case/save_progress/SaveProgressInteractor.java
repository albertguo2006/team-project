package use_case.save_progress;

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
            saveProgressPresenter.prepareSuccessView();
        }
        catch (IOException e) {
            saveProgressPresenter.prepareFailView();
            throw e;
        }
    }
}



