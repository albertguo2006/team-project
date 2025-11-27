package data_access;

import entity.Item;
import entity.WorldItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data_access.LoadFileUserDataAccessObject.JSONFileReader;

/**
 * Data access object for world items - items placed in the game world.
 * Loads item placements from world_items.json.
 */
public class WorldItemDataAccessObject {
    public static final String WORLD_ITEMS_FILE = "src/main/resources/world_items.json";

    private final Map<String, Item> itemMap;
    private final List<WorldItem> mapItems;
    private final List<WorldItem> storeItems;

    public WorldItemDataAccessObject(Map<String, Item> itemMap) {
        this.itemMap = itemMap;
        this.mapItems = new ArrayList<>();
        this.storeItems = new ArrayList<>();
        loadWorldItems();
    }

    private void loadWorldItems() {
        try {
            JSONArray data = JSONFileReader(WORLD_ITEMS_FILE);
            JSONObject root = data.getJSONObject(0);

            // Load map items (free items scattered around the world)
            if (root.has("mapItems")) {
                JSONArray mapItemsArray = root.getJSONArray("mapItems");
                for (int i = 0; i < mapItemsArray.length(); i++) {
                    JSONObject itemData = mapItemsArray.getJSONObject(i);
                    String itemName = itemData.getString("itemName");
                    Item item = itemMap.get(itemName);
                    if (item != null) {
                        WorldItem worldItem = new WorldItem(
                            item,
                            itemData.getString("zone"),
                            itemData.getDouble("x"),
                            itemData.getDouble("y"),
                            false  // Not a store item
                        );
                        mapItems.add(worldItem);
                    }
                }
            }

            // Load store items (items in the Grocery Store that cost money)
            if (root.has("storeItems")) {
                JSONArray storeItemsArray = root.getJSONArray("storeItems");
                for (int i = 0; i < storeItemsArray.length(); i++) {
                    JSONObject itemData = storeItemsArray.getJSONObject(i);
                    String itemName = itemData.getString("itemName");
                    Item item = itemMap.get(itemName);
                    if (item != null) {
                        WorldItem worldItem = new WorldItem(
                            item,
                            "Grocery Store",  // Store items are always in the Grocery Store
                            itemData.getDouble("x"),
                            itemData.getDouble("y"),
                            true  // Is a store item
                        );
                        storeItems.add(worldItem);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load world items: " + e.getMessage());
        }
    }

    /**
     * Gets all items in a specific zone (both map items and store items if applicable).
     * @param zoneName the name of the zone
     * @return list of world items in that zone
     */
    public List<WorldItem> getItemsInZone(String zoneName) {
        List<WorldItem> items = new ArrayList<>();

        // Add map items in this zone
        for (WorldItem item : mapItems) {
            if (item.getZoneName().equals(zoneName) && !item.isCollected()) {
                items.add(item);
            }
        }

        // Add store items if in Grocery Store
        if ("Grocery Store".equals(zoneName)) {
            items.addAll(storeItems);  // Store items don't get collected, they're always available
        }

        return items;
    }

    /**
     * Gets all map items (non-store items).
     */
    public List<WorldItem> getMapItems() {
        return new ArrayList<>(mapItems);
    }

    /**
     * Gets all store items.
     */
    public List<WorldItem> getStoreItems() {
        return new ArrayList<>(storeItems);
    }

    /**
     * Marks a map item as collected.
     * @param worldItem the item to mark as collected
     */
    public void collectItem(WorldItem worldItem) {
        if (!worldItem.isStoreItem()) {
            worldItem.setCollected(true);
        }
    }

    /**
     * Adds a dropped item to the world at the specified location.
     * The item can be picked up again by the player.
     * @param item the item to drop
     * @param zoneName the zone where the item is dropped
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void addDroppedItem(Item item, String zoneName, double x, double y) {
        WorldItem worldItem = new WorldItem(item, zoneName, x, y, false);
        mapItems.add(worldItem);
    }
}
