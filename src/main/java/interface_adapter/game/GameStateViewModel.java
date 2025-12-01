package interface_adapter.game;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

import entity.Item;
import entity.NPC;
import entity.WorldItem;
import use_case.Direction;

/**
 * GameStateViewModel holds the current game state for rendering.
 *
 * This ViewModel follows Clean Architecture by providing a boundary between
 * the use case layer and the view layer. The view observes this ViewModel
 * instead of directly accessing entities.
 *
 * Note: For pragmatic reasons, some entity types (NPC, WorldItem, Item) are
 * exposed directly. A stricter implementation would use DTOs for all data.
 */
public class GameStateViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    // Player state
    private double playerX;
    private double playerY;
    private double playerBalance;
    private int playerHealth;
    private String currentDayProgress;
    private boolean isMoving;
    private Direction currentDirection;
    private Map<Integer, Item> inventory;

    // Zone state
    private String currentZoneName;
    private Color zoneBackgroundColor;
    private String zoneBackgroundImagePath;
    private String zoneBackgroundMusicPath;

    // Proximity state
    private NPC nearbyNPC;
    private WorldItem nearbyWorldItem;
    private boolean inSleepZone;
    private boolean inStockTradingZone;
    private boolean inMailboxZone;

    // UI state
    private int selectedInventorySlot = -1;

    // Getters
    public double getPlayerX() { return playerX; }
    public double getPlayerY() { return playerY; }
    public double getPlayerBalance() { return playerBalance; }
    public int getPlayerHealth() { return playerHealth; }
    public String getCurrentDayProgress() { return currentDayProgress; }
    public boolean isMoving() { return isMoving; }
    public Direction getCurrentDirection() { return currentDirection; }
    public Map<Integer, Item> getInventory() { return inventory; }

    public String getCurrentZoneName() { return currentZoneName; }
    public Color getZoneBackgroundColor() { return zoneBackgroundColor; }
    public String getZoneBackgroundImagePath() { return zoneBackgroundImagePath; }
    public String getZoneBackgroundMusicPath() { return zoneBackgroundMusicPath; }

    public NPC getNearbyNPC() { return nearbyNPC; }
    public WorldItem getNearbyWorldItem() { return nearbyWorldItem; }
    public boolean isInSleepZone() { return inSleepZone; }
    public boolean isInStockTradingZone() { return inStockTradingZone; }
    public boolean isInMailboxZone() { return inMailboxZone; }

    public int getSelectedInventorySlot() { return selectedInventorySlot; }

    // Setters with property change notification
    public void setPlayerX(double playerX) {
        double old = this.playerX;
        this.playerX = playerX;
        support.firePropertyChange("playerX", old, playerX);
    }

    public void setPlayerY(double playerY) {
        double old = this.playerY;
        this.playerY = playerY;
        support.firePropertyChange("playerY", old, playerY);
    }

    public void setPlayerBalance(double playerBalance) {
        double old = this.playerBalance;
        this.playerBalance = playerBalance;
        support.firePropertyChange("playerBalance", old, playerBalance);
    }

    public void setPlayerHealth(int playerHealth) {
        int old = this.playerHealth;
        this.playerHealth = playerHealth;
        support.firePropertyChange("playerHealth", old, playerHealth);
    }

    public void setCurrentDayProgress(String currentDayProgress) {
        String old = this.currentDayProgress;
        this.currentDayProgress = currentDayProgress;
        support.firePropertyChange("currentDayProgress", old, currentDayProgress);
    }

    public void setMoving(boolean moving) {
        boolean old = this.isMoving;
        this.isMoving = moving;
        support.firePropertyChange("isMoving", old, moving);
    }

    public void setCurrentDirection(Direction currentDirection) {
        Direction old = this.currentDirection;
        this.currentDirection = currentDirection;
        support.firePropertyChange("currentDirection", old, currentDirection);
    }

    public void setInventory(Map<Integer, Item> inventory) {
        Map<Integer, Item> old = this.inventory;
        this.inventory = inventory;
        support.firePropertyChange("inventory", old, inventory);
    }

    public void setCurrentZoneName(String currentZoneName) {
        String old = this.currentZoneName;
        this.currentZoneName = currentZoneName;
        support.firePropertyChange("currentZoneName", old, currentZoneName);
    }

    public void setZoneBackgroundColor(Color zoneBackgroundColor) {
        Color old = this.zoneBackgroundColor;
        this.zoneBackgroundColor = zoneBackgroundColor;
        support.firePropertyChange("zoneBackgroundColor", old, zoneBackgroundColor);
    }

    public void setZoneBackgroundImagePath(String zoneBackgroundImagePath) {
        String old = this.zoneBackgroundImagePath;
        this.zoneBackgroundImagePath = zoneBackgroundImagePath;
        support.firePropertyChange("zoneBackgroundImagePath", old, zoneBackgroundImagePath);
    }

    public void setZoneBackgroundMusicPath(String zoneBackgroundMusicPath) {
        String old = this.zoneBackgroundMusicPath;
        this.zoneBackgroundMusicPath = zoneBackgroundMusicPath;
        support.firePropertyChange("zoneBackgroundMusicPath", old, zoneBackgroundMusicPath);
    }

    public void setNearbyNPC(NPC nearbyNPC) {
        NPC old = this.nearbyNPC;
        this.nearbyNPC = nearbyNPC;
        support.firePropertyChange("nearbyNPC", old, nearbyNPC);
    }

    public void setNearbyWorldItem(WorldItem nearbyWorldItem) {
        WorldItem old = this.nearbyWorldItem;
        this.nearbyWorldItem = nearbyWorldItem;
        support.firePropertyChange("nearbyWorldItem", old, nearbyWorldItem);
    }

    public void setInSleepZone(boolean inSleepZone) {
        boolean old = this.inSleepZone;
        this.inSleepZone = inSleepZone;
        support.firePropertyChange("inSleepZone", old, inSleepZone);
    }

    public void setInStockTradingZone(boolean inStockTradingZone) {
        boolean old = this.inStockTradingZone;
        this.inStockTradingZone = inStockTradingZone;
        support.firePropertyChange("inStockTradingZone", old, inStockTradingZone);
    }

    public void setInMailboxZone(boolean inMailboxZone) {
        boolean old = this.inMailboxZone;
        this.inMailboxZone = inMailboxZone;
        support.firePropertyChange("inMailboxZone", old, inMailboxZone);
    }

    public void setSelectedInventorySlot(int selectedInventorySlot) {
        int old = this.selectedInventorySlot;
        this.selectedInventorySlot = selectedInventorySlot;
        support.firePropertyChange("selectedInventorySlot", old, selectedInventorySlot);
    }

    // Property change support
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
