package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private String name;
    private double balance;
    private final Map<NPC, Integer> relationships = new HashMap<>();
    private final List<Event> events =  new ArrayList<>();

    public Player(String name) {
        this.name = name;
        balance = 1000.0;
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

    public List<Event> getEvents() {
        return events;
    }

}
