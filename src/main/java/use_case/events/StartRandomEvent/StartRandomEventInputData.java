package use_case.events.StartRandomEvent;
import entity.Event;

import java.util.ArrayList;

public class StartRandomEventInputData {
    ArrayList<Event> events;
    public StartRandomEventInputData(ArrayList<Event> events){
        this.events = events;
    }
    public Event getEvent(int index){
        return events.get(index);
    }
    public int  getSize(){
        return events.size();
    }


    }

