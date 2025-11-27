package data_access;

import entity.Item;
import entity.Quest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data_access.LoadFileUserDataAccessObject.JSONFileReader;

/**
 * Data access object for quests.
 * Loads quest definitions from quests.json.
 */
public class QuestDataAccessObject {
    public static final String QUESTS_FILE = "src/main/resources/quests.json";

    private final List<Quest> quests;
    private final Map<String, List<Quest>> questsByNPC;
    private final Map<String, Item> itemMap;

    public QuestDataAccessObject(Map<String, Item> itemMap) {
        this.itemMap = itemMap;
        this.quests = new ArrayList<>();
        this.questsByNPC = new HashMap<>();
        loadQuests();
    }

    private void loadQuests() {
        try {
            JSONArray data = JSONFileReader(QUESTS_FILE);
            for (int i = 0; i < data.length(); i++) {
                JSONObject questData = data.getJSONObject(i);
                Quest quest = new Quest(
                    questData.getString("id"),
                    questData.getString("npcName"),
                    questData.getString("requiredItemName"),
                    questData.getString("description"),
                    questData.optInt("moneyReward", 0),
                    questData.isNull("itemReward") ? null : questData.optString("itemReward", null),
                    questData.optInt("relationshipReward", 0)
                );
                quests.add(quest);

                // Index by NPC name
                String npcName = quest.getNpcName();
                questsByNPC.computeIfAbsent(npcName, k -> new ArrayList<>()).add(quest);
            }
        } catch (IOException e) {
            System.err.println("Failed to load quests: " + e.getMessage());
        }
    }

    /**
     * Gets all quests for a specific NPC.
     * @param npcName the NPC's name
     * @return list of quests for that NPC
     */
    public List<Quest> getQuestsByNPC(String npcName) {
        return questsByNPC.getOrDefault(npcName, new ArrayList<>());
    }

    /**
     * Gets an active (not completed) quest for an NPC that the player can complete.
     * @param npcName the NPC's name
     * @param playerInventory the player's inventory (map of slot to item)
     * @return the first active quest that the player has the required item for, or null
     */
    public Quest getActiveQuestForNPC(String npcName, Map<Integer, Item> playerInventory) {
        List<Quest> npcQuests = getQuestsByNPC(npcName);
        for (Quest quest : npcQuests) {
            if (!quest.isCompleted()) {
                // Check if player has the required item
                for (Item item : playerInventory.values()) {
                    if (item.getName().equals(quest.getRequiredItemName())) {
                        return quest;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Marks a quest as completed.
     * @param questId the quest ID to mark as completed
     */
    public void completeQuest(String questId) {
        for (Quest quest : quests) {
            if (quest.getId().equals(questId)) {
                quest.setCompleted(true);
                break;
            }
        }
    }

    /**
     * Gets a quest by its ID.
     * @param questId the quest ID
     * @return the quest, or null if not found
     */
    public Quest getQuestById(String questId) {
        for (Quest quest : quests) {
            if (quest.getId().equals(questId)) {
                return quest;
            }
        }
        return null;
    }

    /**
     * Gets the Item entity for an item name.
     * @param itemName the item name
     * @return the Item, or null if not found
     */
    public Item getItem(String itemName) {
        return itemMap.get(itemName);
    }

    /**
     * Gets all quests.
     */
    public List<Quest> getAllQuests() {
        return new ArrayList<>(quests);
    }
}
