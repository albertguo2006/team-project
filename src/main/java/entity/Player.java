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
    
    // Day system properties
    private Day currentDay;
    private boolean hasSleptToday;
    private int health;
    private double dailyEarnings;
    private double dailySpending;

    public Player(String name) {
        this.name = name;
        balance = 1000.0;
        stats.put("Hunger", 100);
        stats.put("Energy", 100);
        stats.put("Mood", 100);
        // Updated for 1920x1200 virtual resolution
        this.x = 960.0;  // Center of 1920 width
        this.y = 600.0;  // Center of 1200 height
        this.speed = 360.0;  // Scaled up for 1920x1200 (was 150 for 800x600)
        
        // Initialize day system
        this.currentDay = Day.MONDAY;
        this.hasSleptToday = false;
        this.health = 100;
        this.dailyEarnings = 0.0;
        this.dailySpending = 0.0;
    }

    public Player (String name, double balance, double x, double y, Map<String, Integer> stats) {
        this.name = name;
        this.balance = balance;
        this.x = x;
        this.y = y;
        this.speed = 360.0;  // Scaled up for 1920x1200 virtual resolution
        this.stats.putAll(stats);
        
        // Initialize day system with defaults
        this.currentDay = Day.MONDAY;
        this.hasSleptToday = false;
        this.health = 100;
        this.dailyEarnings = 0.0;
        this.dailySpending = 0.0;
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

    public void setStat(String stat, int score){ stats.put(stat, score); }

    public void setStats(int hunger, int energy, int mood) {
        stats.put("Hunger", hunger);
        stats.put("Energy", energy);
        stats.put("Mood", mood);
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

    // Overload method for addInventory when we know which index to fill
    public void addInventory(int index, Item item){
        inventory.put(index, item);
    }

    public void removeInventory(int index){
        inventory.remove(index);
        // Assuming we always know the index of the item the Player wants to remove based on the user's
        // interactions with the UI.
    }

    public void itemUsed(int index) {
        Item item = inventory.get(index);
        if (item.getType().equals("Speed")){
            setSpeed(speed + item.getScore());
        }
        else{
            setStat(item.getType(), item.getScore());
        }
    }


    public Map<String, Integer> getStats(){
        return stats;
    }

    public int getNPCScore(NPC npc) {
        return relationships.get(npc);
    }

    public void addNPC(NPC npc) {
        if (!relationships.containsKey(npc)) {
            relationships.put(npc, npc.getDefaultScore());
        }
    }

    public void addNPCScore(NPC npc, int score) {
        relationships.put(npc, score);
    }

    public void removeNPC(NPC npc) { relationships.remove(npc); }

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

    public Portfolio getPortfolio() { return portfolio; };

    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }

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
    
    // Day system getters and setters
    
    /**
     * Gets the current day of the week.
     * @return the current Day
     */
    public Day getCurrentDay() {
        return currentDay;
    }
    
    /**
     * Sets the current day.
     * @param day the day to set
     */
    public void setCurrentDay(Day day) {
        this.currentDay = day;
    }
    
    /**
     * Checks if the player has slept today.
     * @return true if already slept, false otherwise
     */
    public boolean hasSleptToday() {
        return hasSleptToday;
    }
    
    /**
     * Sets whether the player has slept today.
     * @param slept true if slept, false otherwise
     */
    public void setHasSleptToday(boolean slept) {
        this.hasSleptToday = slept;
    }
    
    /**
     * Gets the player's health (0-100).
     * @return current health
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Sets the player's health.
     * @param health the health value (0-100)
     */
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(100, health));
    }
    
    /**
     * Gets the daily earnings accumulated today.
     * @return daily earnings
     */
    public double getDailyEarnings() {
        return dailyEarnings;
    }
    
    /**
     * Adds to the daily earnings.
     * @param amount the amount to add
     */
    public void addDailyEarnings(double amount) {
        this.dailyEarnings += amount;
    }
    
    /**
     * Gets the daily spending accumulated today.
     * @return daily spending
     */
    public double getDailySpending() {
        return dailySpending;
    }
    
    /**
     * Adds to the daily spending.
     * @param amount the amount to add
     */
    public void addDailySpending(double amount) {
        this.dailySpending += amount;
    }
    
    /**
     * Resets daily financial tracking.
     * Called at the start of a new day.
     */
    public void resetDailyFinancials() {
        this.dailyEarnings = 0.0;
        this.dailySpending = 0.0;
    }
    
    /**
     * Advances to the next day and resets daily flags.
     * @return true if successfully advanced, false if already Friday
     */
    public boolean advanceDay() {
        Day nextDay = currentDay.getNext();
        if (nextDay != null) {
            this.currentDay = nextDay;
            this.hasSleptToday = false;
            return true;
        }
        return false;
    }

}