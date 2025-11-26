package entity;

/**
 * Represents an item placed in the game world.
 * Can be either a map item (free to pick up) or a store item (must be purchased).
 */
public class WorldItem {
    private final Item item;
    private final String zoneName;
    private final double x;
    private final double y;
    private boolean collected;
    private final boolean isStoreItem;

    public WorldItem(Item item, String zoneName, double x, double y, boolean isStoreItem) {
        this.item = item;
        this.zoneName = zoneName;
        this.x = x;
        this.y = y;
        this.collected = false;
        this.isStoreItem = isStoreItem;
    }

    public Item getItem() {
        return item;
    }

    public String getZoneName() {
        return zoneName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean isStoreItem() {
        return isStoreItem;
    }

    /**
     * Gets the price of this item. Store items have a price, map items are free.
     */
    public int getPrice() {
        return isStoreItem ? item.getPrice() : 0;
    }

    /**
     * Checks if the player is within pickup range of this item.
     * @param playerX player's X coordinate
     * @param playerY player's Y coordinate
     * @param range the interaction range
     * @return true if player is within range
     */
    public boolean isInRange(double playerX, double playerY, double range) {
        double dx = playerX - x;
        double dy = playerY - y;
        return Math.sqrt(dx * dx + dy * dy) < range;
    }
}
