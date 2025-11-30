package interface_adapter.load_progress;

import use_case.load_progress.LoadProgressOutputBoundary;
import use_case.load_progress.LoadProgressOutputData;

public class LoadProgressPresenter implements LoadProgressOutputBoundary {
    LoadProgressViewModel loadProgressViewModel;

    public LoadProgressPresenter(LoadProgressViewModel loadProgressViewModel) {
        this.loadProgressViewModel = loadProgressViewModel;
    }

    @Override
    public void prepareSuccessView(LoadProgressOutputData data) {
        loadProgressViewModel.setPlayerName(data.getName());
        loadProgressViewModel.setRecentSaveDate(data.getRecentSaveDate());
    }

}
