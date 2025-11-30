package use_case.load_progress;

import entity.Player;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LoadProgressInteractor implements LoadProgressInputBoundary{
    LoadProgressDataAccessInterface loadProgressDataAccessObject;
    LoadProgressOutputBoundary loadProgressPresenter;

    public LoadProgressInteractor(LoadProgressDataAccessInterface loadProgressDataAccessObject,
                                  LoadProgressOutputBoundary loadProgressPresenter) {
        this.loadProgressDataAccessObject = loadProgressDataAccessObject;
        this.loadProgressPresenter = loadProgressPresenter;
    }

    @Override
    public Player loadGame(LoadProgressInputData loadProgressInputData) throws IOException {
        try{
            Player player = loadProgressDataAccessObject.load(loadProgressInputData.getGameMap(),
                    loadProgressInputData.getFileName());
            LoadProgressOutputData outputData = new LoadProgressOutputData(player.getName(),
                    loadProgressDataAccessObject.getRecentSaveDate());
            loadProgressPresenter.prepareSuccessView(outputData);
            return player;
        }
        catch (IOException e){
            throw new FileNotFoundException(e.getMessage());
        }
    }

}
