package entity;

/**
 * Represents a quest that an NPC can give to the player.
 * Quests involve delivering items to NPCs in exchange for rewards.
 */
public class Quest {
    private final String id;
    private final String npcName;
    private final String requiredItemName;
    private final String description;
    private final int moneyReward;
    private final String itemReward;  // Item name or null
    private final int relationshipReward;
    private boolean completed;

    public Quest(String id, String npcName, String requiredItemName, String description,
                 int moneyReward, String itemReward, int relationshipReward) {
        this.id = id;
        this.npcName = npcName;
        this.requiredItemName = requiredItemName;
        this.description = description;
        this.moneyReward = moneyReward;
        this.itemReward = itemReward;
        this.relationshipReward = relationshipReward;
        this.completed = false;
    }

    public String getId() {
        return id;
    }

    public String getNpcName() {
        return npcName;
    }

    public String getRequiredItemName() {
        return requiredItemName;
    }

    public String getDescription() {
        return description;
    }

    public int getMoneyReward() {
        return moneyReward;
    }

    public String getItemReward() {
        return itemReward;
    }

    public int getRelationshipReward() {
        return relationshipReward;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Gets a formatted description of the quest rewards.
     */
    public String getRewardDescription() {
        StringBuilder sb = new StringBuilder();
        if (moneyReward > 0) {
            sb.append("$").append(moneyReward);
        }
        if (itemReward != null && !itemReward.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(itemReward);
        }
        if (relationshipReward > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("+").append(relationshipReward).append(" relationship");
        }
        return sb.toString();
    }
}
