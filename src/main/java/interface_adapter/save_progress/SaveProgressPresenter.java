package interface_adapter.save_progress;

import use_case.save_progress.SaveProgressOutputBoundary;
import use_case.save_progress.SaveProgressOutputData;

public class SaveProgressPresenter implements SaveProgressOutputBoundary {
    SaveProgressViewModel saveProgressViewModel;

    public SaveProgressPresenter(SaveProgressViewModel saveProgressViewModel) {
        this.saveProgressViewModel = saveProgressViewModel;
    }

    @Override
    public void prepareSuccessView(SaveProgressOutputData saveProgressOutputData) {
        saveProgressViewModel.setData(saveProgressOutputData.getPlayerName(), saveProgressOutputData.getCurrentDay());
    }
}
