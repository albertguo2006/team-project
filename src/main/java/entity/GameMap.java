package entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the entire game map, containing all zones and their connections.
 * Also manages special transitions like subway tunnels.
 */
public class GameMap {
    private final Map<String, Zone> zones = new HashMap<>();
    private final List<Transition> specialTransitions = new ArrayList<>();
    private String currentZoneName;

    public GameMap() {
        // Initialize all zones and their connections
        createZones();
        currentZoneName = "Home";
    }

    private void createZones() {
        // Zone colors for visual distinction (fallback if images don't load)
        Color homeColor = new Color(220, 220, 255);
        Color subwayColor = new Color(200, 255, 255);
        Color streetColor = new Color(200, 255, 200);
        Color groceryColor = new Color(255, 255, 200);
        Color officeColor = new Color(255, 220, 200);
        Color lobbyColor = new Color(255, 240, 220);

        // Music paths (WAV format for Java AudioSystem compatibility)
        String lofiMusic = "/audio/lofi-lofi-song-2-434695.wav";
        String elevatorMusic = "/audio/local-forecast-elevator.wav";

        // Create zones with background images and music
        Zone home = new Zone("Home", homeColor, "/backgrounds/home.png", lofiMusic);
        Zone subway1 = new Zone("Subway Station 1", subwayColor, "/backgrounds/subway_1.png", lofiMusic);
        Zone street1 = new Zone("Street 1", streetColor, "/backgrounds/street_1.png", lofiMusic);
        Zone street2 = new Zone("Street 2", streetColor, "/backgrounds/street_2.png", lofiMusic);
        Zone grocery = new Zone("Grocery Store", groceryColor, "/backgrounds/store.png", lofiMusic);
        Zone subway2 = new Zone("Subway Station 2", subwayColor, "/backgrounds/subway_2.png", lofiMusic);
        Zone office = new Zone("Office (Your Cubicle)", officeColor, "/backgrounds/office.png", elevatorMusic);
        Zone street3 = new Zone("Street 3", streetColor, "/backgrounds/street_3.png", lofiMusic);
        Zone lobby = new Zone("Office Lobby", lobbyColor, "/backgrounds/office_lobby.png", elevatorMusic);

        // Set neighbors (edges) - Based on the map layout
        // Each connection must be bidirectional for proper zone transitions
        
        // Home <-> Street 1
        home.setNeighbor(Zone.Edge.DOWN, "Street 1");
        street1.setNeighbor(Zone.Edge.UP, "Home");

        // Street 1 <-> Street 2
        street1.setNeighbor(Zone.Edge.RIGHT, "Street 2");
        street2.setNeighbor(Zone.Edge.LEFT, "Street 1");
        
        // Street 1 <-> Subway Station 1
        street1.setNeighbor(Zone.Edge.DOWN, "Subway Station 1");
        subway1.setNeighbor(Zone.Edge.UP, "Street 1");

        // Subway Station 1 <-> Subway Station 2
        // NOTE: These use special zone-based transitions, not edge-based
        // Handled specially in GamePanel with 300-pixel trigger zones

        // Street 2 <-> Grocery Store
        street2.setNeighbor(Zone.Edge.RIGHT, "Grocery Store");
        grocery.setNeighbor(Zone.Edge.LEFT, "Street 2");

        // Subway Station 2 <-> Street 3
        subway2.setNeighbor(Zone.Edge.DOWN, "Street 3");
        street3.setNeighbor(Zone.Edge.UP, "Subway Station 2");

        // Street 3 <-> Office Lobby
        street3.setNeighbor(Zone.Edge.RIGHT, "Office Lobby");
        lobby.setNeighbor(Zone.Edge.LEFT, "Street 3");
        
        // Office Lobby <-> Office (Your Cubicle)
        lobby.setNeighbor(Zone.Edge.RIGHT, "Office (Your Cubicle)");
        office.setNeighbor(Zone.Edge.LEFT, "Office Lobby");

        // Add zones to map
        zones.put(home.getName(), home);
        zones.put(subway1.getName(), subway1);
        zones.put(street1.getName(), street1);
        zones.put(street2.getName(), street2);
        zones.put(grocery.getName(), grocery);
        zones.put(subway2.getName(), subway2);
        zones.put(office.getName(), office);
        zones.put(street3.getName(), street3);
        zones.put(lobby.getName(), lobby);
    }


    public Zone getCurrentZone() {
        return zones.get(currentZoneName);
    }
    
    public void setCurrentZone(String zoneName) {
        if (zones.containsKey(zoneName)) {
            System.out.println("GameMap: Transitioning to zone: " + zoneName);
            currentZoneName = zoneName;
        } else {
            System.err.println("ERROR: Attempted to transition to non-existent zone: '" + zoneName + "'");
            System.err.println("Available zones: " + zones.keySet());
        }
    }
    
    public Zone getZone(String zoneName) {
        return zones.get(zoneName);
    }

    /**
     * Gets a special transition from the current zone in a given direction.
     * Returns null if no special transition exists for that direction.
     * 
     * @param edge the direction to check
     * @return the Transition object if one exists, or null
     */
    public Transition getSpecialTransition(Zone.Edge edge) {
        for (Transition t : specialTransitions) {
            if (t.getFromZone().equals(currentZoneName) && t.getFromEdge() == edge) {
                return t;
            }
        }
        return null;
    }

    public List<Transition> getAllTransitions() {
        return specialTransitions;
    }
}
