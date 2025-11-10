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
    
    // Movement properties (for Use Case 1: Environment Interaction)
    private double x;
    private double y;
    private double speed;  // pixels per second

    public Player(String name) {
        this.name = name;
        balance = 1000.0;
        stats.put("Hunger", 100);
        stats.put("Energy", 100);
        stats.put("Mood", 100);
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
        stats.put("Hunger", hunger);
        stats.put("Energy", energy);
        stats.put("Mood", mood);
    }

    public Map<String, Integer> getStats(){
        return stats;
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
