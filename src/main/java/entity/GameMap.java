package entity;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the entire game map, containing all zones and their connections.
 */
public class GameMap {
    private final Map<String, Zone> zones = new HashMap<>();
    private String currentZoneName;

    public GameMap() {
        // Initialize all zones and their connections
        createZones();
        currentZoneName = "Home";
    }

    private void createZones() {
        // Zone colors for visual distinction
        Color homeColor = new Color(220, 220, 255);
        Color subwayColor = new Color(200, 255, 255);
        Color streetColor = new Color(200, 255, 200);
        Color groceryColor = new Color(255, 255, 200);
        Color officeColor = new Color(255, 220, 200);
        Color lobbyColor = new Color(255, 240, 220);

        // Create zones
        Zone home = new Zone("Home", homeColor);
        Zone subway1 = new Zone("Subway Station 1", subwayColor);
        Zone street1 = new Zone("Street 1", streetColor);
        Zone street2 = new Zone("Street 2", streetColor);
        Zone grocery = new Zone("Grocery Store", groceryColor);
        Zone subway2 = new Zone("Subway Station 2", subwayColor);
        Zone office = new Zone("Office (Your Cubicle)", officeColor);
        Zone street3 = new Zone("Street 3", streetColor);
        Zone lobby = new Zone("Office Lobby", lobbyColor);

        // Set neighbors (edges) - Based on the map layout
        // Home (top-left) - connects DOWN to Street 1, RIGHT is blocked (x)
        home.setNeighbor(Zone.Edge.DOWN, "Street 1");

        // Subway Station 1 (top-right) - connects DOWN to Street 2, LEFT is blocked (x)
        subway1.setNeighbor(Zone.Edge.DOWN, "Street 2");

        // Street 1 - connects UP to Home, RIGHT to Street 2, DOWN to Subway Station 2
        street1.setNeighbor(Zone.Edge.UP, "Home");
        street1.setNeighbor(Zone.Edge.RIGHT, "Street 2");
        street1.setNeighbor(Zone.Edge.DOWN, "Subway Station 2");

        // Street 2 - connects UP to Subway Station 1, LEFT to Street 1, DOWN to Office, RIGHT to Grocery Store
        street2.setNeighbor(Zone.Edge.UP, "Subway Station 1");
        street2.setNeighbor(Zone.Edge.LEFT, "Street 1");
        street2.setNeighbor(Zone.Edge.DOWN, "Office (Your Cubicle)");
        street2.setNeighbor(Zone.Edge.RIGHT, "Grocery Store");

        // Grocery Store - connects LEFT to Street 2
        grocery.setNeighbor(Zone.Edge.LEFT, "Street 2");

        // Subway Station 2 - connects UP to Street 1, DOWN to Street 3, RIGHT is blocked (x)
        subway2.setNeighbor(Zone.Edge.UP, "Street 1");
        subway2.setNeighbor(Zone.Edge.DOWN, "Street 3");

        // Office (Your Cubicle) - connects UP to Street 2, DOWN to Office Lobby, LEFT is blocked (x)
        office.setNeighbor(Zone.Edge.UP, "Street 2");
        office.setNeighbor(Zone.Edge.DOWN, "Office Lobby");

        // Street 3 - connects UP to Subway Station 2, RIGHT to Office Lobby
        street3.setNeighbor(Zone.Edge.UP, "Subway Station 2");
        street3.setNeighbor(Zone.Edge.RIGHT, "Office Lobby");

        // Office Lobby - connects UP to Office, LEFT to Street 3
        lobby.setNeighbor(Zone.Edge.UP, "Office (Your Cubicle)");
        lobby.setNeighbor(Zone.Edge.LEFT, "Street 3");

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
            currentZoneName = zoneName;
        }
    }
    public Zone getZone(String zoneName) {
        return zones.get(zoneName);
    }
}
