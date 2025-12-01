package use_case.proximity;

import java.util.List;

import data_access.NPCDataAccessObject;
import data_access.WorldItemDataAccessObject;
import entity.NPC;
import entity.WorldItem;

/**
 * ProximityDetectionInteractor handles the business logic for proximity detection.
 *
 * This use case was extracted from GamePanel to follow Clean Architecture principles.
 * It centralizes all proximity detection logic for NPCs, items, and special zones.
 */
public class ProximityDetectionInteractor implements ProximityDetectionInputBoundary {

    // Virtual resolution constants
    private static final double VIRTUAL_WIDTH = 1920.0;
    private static final double VIRTUAL_HEIGHT = 1200.0;

    // Sleep zone (top-right 400x400px in Home)
    private static final int SLEEP_ZONE_X = (int) VIRTUAL_WIDTH - 400;
    private static final int SLEEP_ZONE_Y = 0;
    private static final int SLEEP_ZONE_WIDTH = 400;
    private static final int SLEEP_ZONE_HEIGHT = 400;

    // Stock trading zone (in Office cubicle)
    private static final int STOCK_TRADING_ZONE_X = 1188;
    private static final int STOCK_TRADING_ZONE_Y = 244;
    private static final int STOCK_TRADING_ZONE_WIDTH = 239;
    private static final int STOCK_TRADING_ZONE_HEIGHT = 226;

    // Mailbox zone (bottom middle of Home)
    private static final int MAILBOX_ZONE_X = 760;
    private static final int MAILBOX_ZONE_Y = 900;
    private static final int MAILBOX_ZONE_WIDTH = 400;
    private static final int MAILBOX_ZONE_HEIGHT = 300;

    // Interaction radii
    private static final double NPC_INTERACTION_RADIUS = 100.0;
    private static final double ITEM_INTERACTION_RADIUS = 80.0;

    private final NPCDataAccessObject npcDataAccess;
    private WorldItemDataAccessObject worldItemDataAccess;

    // Current proximity state
    private NPC nearbyNPC;
    private WorldItem nearbyWorldItem;
    private boolean inSleepZone;
    private boolean inStockTradingZone;
    private boolean inMailboxZone;

    /**
     * Constructs a ProximityDetectionInteractor.
     *
     * @param npcDataAccess the NPC data access object
     */
    public ProximityDetectionInteractor(NPCDataAccessObject npcDataAccess) {
        this.npcDataAccess = npcDataAccess;
    }

    /**
     * Sets the world item data access object.
     *
     * @param worldItemDataAccess the world item data access object
     */
    public void setWorldItemDataAccess(WorldItemDataAccessObject worldItemDataAccess) {
        this.worldItemDataAccess = worldItemDataAccess;
    }

    @Override
    public void checkAllProximities(double playerX, double playerY, String currentZoneName) {
        checkSleepZone(playerX, playerY, currentZoneName);
        checkStockTradingZone(playerX, playerY, currentZoneName);
        checkMailboxZone(playerX, playerY, currentZoneName);
        checkNPCProximity(playerX, playerY, currentZoneName);
        checkWorldItemProximity(playerX, playerY, currentZoneName);
    }

    @Override
    public NPC getNearbyNPC() {
        return nearbyNPC;
    }

    @Override
    public WorldItem getNearbyWorldItem() {
        return nearbyWorldItem;
    }

    @Override
    public boolean isInSleepZone() {
        return inSleepZone;
    }

    @Override
    public boolean isInStockTradingZone() {
        return inStockTradingZone;
    }

    @Override
    public boolean isInMailboxZone() {
        return inMailboxZone;
    }

    /**
     * Checks if the player is in the sleep zone.
     */
    private void checkSleepZone(double playerX, double playerY, String currentZoneName) {
        if (!"Home".equals(currentZoneName)) {
            inSleepZone = false;
            return;
        }

        inSleepZone = playerX >= SLEEP_ZONE_X &&
                      playerX <= SLEEP_ZONE_X + SLEEP_ZONE_WIDTH &&
                      playerY >= SLEEP_ZONE_Y &&
                      playerY <= SLEEP_ZONE_Y + SLEEP_ZONE_HEIGHT;
    }

    /**
     * Checks if the player is in the stock trading zone.
     */
    private void checkStockTradingZone(double playerX, double playerY, String currentZoneName) {
        if (!"Office (Your Cubicle)".equals(currentZoneName)) {
            inStockTradingZone = false;
            return;
        }

        inStockTradingZone = playerX >= STOCK_TRADING_ZONE_X &&
                             playerX <= STOCK_TRADING_ZONE_X + STOCK_TRADING_ZONE_WIDTH &&
                             playerY >= STOCK_TRADING_ZONE_Y &&
                             playerY <= STOCK_TRADING_ZONE_Y + STOCK_TRADING_ZONE_HEIGHT;
    }

    /**
     * Checks if the player is in the mailbox zone.
     */
    private void checkMailboxZone(double playerX, double playerY, String currentZoneName) {
        if (!"Home".equals(currentZoneName)) {
            inMailboxZone = false;
            return;
        }

        inMailboxZone = playerX >= MAILBOX_ZONE_X &&
                        playerX <= MAILBOX_ZONE_X + MAILBOX_ZONE_WIDTH &&
                        playerY >= MAILBOX_ZONE_Y &&
                        playerY <= MAILBOX_ZONE_Y + MAILBOX_ZONE_HEIGHT;
    }

    /**
     * Checks if the player is near any NPC in the current zone.
     */
    private void checkNPCProximity(double playerX, double playerY, String currentZoneName) {
        if (currentZoneName == null || npcDataAccess == null) {
            nearbyNPC = null;
            return;
        }

        List<NPC> npcsInZone = npcDataAccess.getNPCsInZone(currentZoneName);
        nearbyNPC = null;

        for (NPC npc : npcsInZone) {
            double distance = Math.sqrt(
                    Math.pow(playerX - npc.getX(), 2) +
                    Math.pow(playerY - npc.getY(), 2)
            );

            if (distance < NPC_INTERACTION_RADIUS) {
                nearbyNPC = npc;
                break;
            }
        }
    }

    /**
     * Checks if the player is near any world item in the current zone.
     */
    private void checkWorldItemProximity(double playerX, double playerY, String currentZoneName) {
        if (worldItemDataAccess == null || currentZoneName == null) {
            nearbyWorldItem = null;
            return;
        }

        List<WorldItem> items = worldItemDataAccess.getItemsInZone(currentZoneName);
        nearbyWorldItem = null;

        for (WorldItem item : items) {
            if (item.isInRange(playerX, playerY, ITEM_INTERACTION_RADIUS)) {
                nearbyWorldItem = item;
                break;
            }
        }
    }
}
