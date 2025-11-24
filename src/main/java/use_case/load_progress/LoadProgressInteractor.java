package use_case.load_progress;

import entity.Player;

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
    public Player loadGame(LoadProgressInputData loadProgressInputData) {
        try{
            Player player = loadProgressDataAccessObject.load(loadProgressInputData.getGameMap(),
                    loadProgressInputData.getFileName());
//          loadProgressPresenter.prepareSuccessView();
            return player;
        }
        catch (IOException e){
            loadProgressPresenter.prepareFailView();
            return null;
        }
    }

}

