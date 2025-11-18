package entity;

public class Item {
    private final String name;
    private final String description;
    private final String type;
    private final int score;

    // 'type' attribute should only be one of the stat options: 'Hunger', 'Energy', or 'Mood'.

    public Item(String name, String description, String type, int score) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public int getScore(){
        return score;
    }
}
