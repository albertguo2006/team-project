package interface_adapter.load_progress;

import use_case.load_progress.LoadProgressOutputBoundary;
import use_case.load_progress.LoadProgressOutputData;

public class LoadProgressPresenter implements LoadProgressOutputBoundary {
    @Override
    public void prepareSuccessView(LoadProgressOutputData data) {

    }

    @Override
    public void prepareFailView() {

    }
}
