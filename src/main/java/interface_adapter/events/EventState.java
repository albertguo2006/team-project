package interface_adapter.events;

import entity.EventOutcome;

import java.util.HashMap;

public class EventState {
    private String name;
    private String description;
    private HashMap<Integer, EventOutcome> outcomes;
    private int index;

    public EventState() {
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setOutcomes(HashMap<Integer, EventOutcome> outcomes) {
        this.outcomes = outcomes;
    }
    public HashMap<Integer, EventOutcome> getOutcomes() {
        return outcomes;
    }
    public int getOutcomeCount() {
        return outcomes.size();
    }
    public String getOutcomeName(int id){
        return outcomes.get(id).getOutcomeName();
    }

    public EventOutcome getOutcome(int id) {
        return outcomes.get(id);
    }

    public void setIndex(int i){
        this.index = i;
    }
    public int getIndex(){
        return index;
    }

}
