package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private String name;
    private double balance;
    private final Map<String, Integer> stats = new HashMap<>();
    private final Map<NPC, Integer> relationships = new HashMap<>();
    private final List<Event> events =  new ArrayList<>();
    private Portfolio portfolio =  new Portfolio();
    Map <Integer, Item> inventory = new HashMap<>();
    // Maps inventory slot number to item in that slot.
    
    // Movement properties (for Use Case 1: Environment Interaction)
    private double x;
    private double y;
    private double speed;  // pixels per second

    public Player(String name) {
        this.name = name;
        balance = 1000.0;
        stats.put("hunger", 100);
        stats.put("energy", 100);
        stats.put("mood", 100);
        this.x = 400.0;  // Default starting position (center-ish)
        this.y = 300.0;
        this.speed = 150.0;  // pixels per second
    }

    public Player (String name, double balance, double x, double y, Map<String, Integer> stats) {
        this.name = name;
        this.balance = balance;
        this.x = x;
        this.y = y;
        this.speed = 150.0;
        this.stats.putAll(stats);
    }

    public void setName(String name) {
        this.name = name;
    }
    // can be deleted if we decide that players shouldn't be able to change their name mid-game.

    public String getName() {
        return name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setStats(int hunger, int energy, int mood) {
        stats.put("hunger", hunger);
        stats.put("energy", energy);
        stats.put("mood", mood);
    }

    public void setStat(String stat, int score){ stats.put(stat, score); }

    public Map<String, Integer> getStats(){
        return stats;
    }

    public Map<Integer, Item> getInventory() { return inventory; }

    public void addInventory(Item item) {
        if (inventory.isEmpty()) {
            inventory.put(1, item);
        }
        else if (inventory.size() == 5){
            System.out.println("Your inventory is full!");
        }
        else {
            for (int i = 1; i <= 5; i++) {
                if (!inventory.containsKey(i)){
                    inventory.put(i, item);
                }
                // Places the item in the first open slot in the player's inventory.
                // For example, if both slots 3 and 5 are empty, the item picked up will be placed in inventory slot 3.
            }
        }
    }

    public void removeInventory(int index){
        inventory.remove(index);
        // Assuming we always know the index of the item the Player wants to remove based on the user's
        // interactions with the UI.
    }

    public void itemUsed(int index) {
        Item item = inventory.get(index);
        setStat(item.getType(), item.getScore());
    }

    public int getNPCScore(NPC npc) {
        return relationships.get(npc);
    }

    public void addNPC(NPC npc) {
        if (!relationships.containsKey(npc)) {
            relationships.put(npc, npc.getDefaultscore());
        }
    }

    public void removeNPC(NPC npc) {
        relationships.remove(npc);
    }

    public void addEvent(Event event) {
        if (!events.contains(event)) {
            events.add(event);
        }
    }

    public Map <NPC, Integer> getRelationships() {
        return relationships;
    }

    public List<Event> getEvents() {
        return events;
    }

    // Movement getters and setters
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
