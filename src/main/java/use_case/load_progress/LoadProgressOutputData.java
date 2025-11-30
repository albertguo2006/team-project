package use_case.load_progress;

public class LoadProgressOutputData {
    String name;
    String recentSaveDate;

    public LoadProgressOutputData(String name, String recentSaveDate) {
        this.name = name;
        this.recentSaveDate = recentSaveDate;
    }

    public String getName() { return name; }

    public String getRecentSaveDate() { return recentSaveDate; }

}
