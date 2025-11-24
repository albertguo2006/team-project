package entity;

import java.util.Objects;

public class Item {
    private final String name;
    private final String description;
    private final String type;
    private final int score;

    // 'type' attribute should only be one of the stat options: 'Hunger', 'Energy', 'Mood', or 'Speed'.

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

    public int getScore(){ return score; }

    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Item)){
            return false;
        }
        Item item = (Item) obj;
        return name.equals(item.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
