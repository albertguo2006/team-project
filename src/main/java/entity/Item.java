package entity;

import java.util.Objects;

public class Item {
    private final String name;
    private final String description;
    private final String type;
    private final int score;
    private final int price;
    private final boolean isConsumable;
    private final String buffType;

    // 'type' attribute should only be one of the stat options: 'Hunger', 'Energy', 'Mood', 'Speed', 'Special', or 'Quest'.
    // 'buffType' is for special buffs like "SpeedBoost" or "StockSlowdown", null for regular stat items.

    public Item(String name, String description, String type, int score) {
        this(name, description, type, score, 0, true, null);
    }

    public Item(String name, String description, String type, int score, int price, boolean isConsumable, String buffType) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.score = score;
        this.price = price;
        this.isConsumable = isConsumable;
        this.buffType = buffType;
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

    public int getPrice() { return price; }

    public boolean isConsumable() { return isConsumable; }

    public String getBuffType() { return buffType; }

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
