package use_case.proximity;

import entity.NPC;
import entity.WorldItem;

/**
 * ProximityDetectionInputBoundary defines the input port for proximity detection operations.
 *
 * This interface follows Clean Architecture principles by abstracting the business logic
 * for detecting when the player is near NPCs, items, or special zones.
 */
public interface ProximityDetectionInputBoundary {

    /**
     * Checks all proximity conditions based on player position and current zone.
     *
     * @param playerX the player's X position
     * @param playerY the player's Y position
     * @param currentZoneName the name of the current zone
     */
    void checkAllProximities(double playerX, double playerY, String currentZoneName);

    /**
     * Gets the NPC that is currently near the player, if any.
     *
     * @return the nearby NPC, or null if no NPC is nearby
     */
    NPC getNearbyNPC();

    /**
     * Gets the world item that is currently near the player, if any.
     *
     * @return the nearby world item, or null if no item is nearby
     */
    WorldItem getNearbyWorldItem();

    /**
     * Checks if the player is in the sleep zone.
     *
     * @return true if in sleep zone, false otherwise
     */
    boolean isInSleepZone();

    /**
     * Checks if the player is in the stock trading zone.
     *
     * @return true if in stock trading zone, false otherwise
     */
    boolean isInStockTradingZone();

    /**
     * Checks if the player is in the mailbox zone.
     *
     * @return true if in mailbox zone, false otherwise
     */
    boolean isInMailboxZone();
}
