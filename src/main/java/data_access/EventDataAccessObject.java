package data_access;

import entity.Event;
import entity.EventOutcome;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeDataAccessInterface;
import use_case.events.StartRandomEvent.StartRandomEventDataAccessInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EventDataAccessObject implements StartRandomEventDataAccessInterface,
        ActivateRandomOutcomeDataAccessInterface {
    private ArrayList<Event> eventList;
    private Player player;
    String eventSource = "events.json";

    public ArrayList<Event> createEventList() {
        ArrayList<Event> events = new ArrayList<>();
        try {
            String jsonString = Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                    .getResource(eventSource)).toURI()));
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject newEvent = jsonArray.getJSONObject(i);
                int eventID = newEvent.getInt("id");
                String eventName = newEvent.getString("name");
                String eventDescription = newEvent.getString("description");
                double probability = newEvent.optDouble("probability", 1.0);
                JSONObject eventOutcomes = newEvent.getJSONObject("outcomes");
                HashMap<Integer, EventOutcome> OutcomeMap = createEventOutcomeList(eventOutcomes);
                events.add(new Event(eventID, eventName, eventDescription, OutcomeMap, probability));
            }
            this.eventList = events;
            return events;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException();
        }
    }

    public HashMap<Integer, EventOutcome> createEventOutcomeList(JSONObject eventOutcomes) {
        HashMap<Integer, EventOutcome> outcomeMap = new HashMap<>();
        for (String key : eventOutcomes.keySet()) {
            JSONObject newOutcome = eventOutcomes.getJSONObject(key);
            String outcomeName = newOutcome.getString("name");
            String outcomeDescription = newOutcome.getString("description");
            double outcomeChance = newOutcome.getDouble("chance");
            int outcomeResult = newOutcome.getInt("result");
            outcomeMap.put(Integer.parseInt(key), new EventOutcome(Integer.parseInt(key), outcomeName,
                    outcomeDescription, outcomeChance, outcomeResult));
        }
        return outcomeMap;
    }

    public Event getEvent(int index) {
        return eventList.get(index);
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public int getSize() {
        return eventList.size();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setBalance(double balance) {
        player.setBalance(balance);
    }

    public double getBalance() {
        return player.getBalance();
    }
}


