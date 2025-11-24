package use_case.load_progress;

public class LoadProgressOutputData {
    String name;
    String errorMessage;

    public LoadProgressOutputData(String name, String errorMessage) {
        this.name = name;
    }

    public String getName() { return name; }

}
