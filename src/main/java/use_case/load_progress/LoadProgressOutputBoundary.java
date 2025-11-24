package use_case.load_progress;

public interface LoadProgressOutputBoundary {
    void prepareSuccessView(LoadProgressOutputData data);

    void prepareFailView();

}
