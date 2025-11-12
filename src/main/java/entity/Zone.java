package entity;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single zone (screen) in the game map.
 */
public class Zone {
    public enum Edge { UP, DOWN, LEFT, RIGHT }

    private final String name;
    private final Color backgroundColor;
    private final String backgroundImagePath;
    private final Map<Edge, String> neighbors = new HashMap<>();

    public Zone(String name, Color backgroundColor, String backgroundImagePath) {
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.backgroundImagePath = backgroundImagePath;
    }

    public String getName() { return name; }
    public Color getBackgroundColor() { return backgroundColor; }
    public String getBackgroundImagePath() { return backgroundImagePath; }

    public void setNeighbor(Edge edge, String neighborZoneName) {
        neighbors.put(edge, neighborZoneName);
    }
    public String getNeighbor(Edge edge) {
        return neighbors.get(edge);
    }
    public Map<Edge, String> getNeighbors() { return neighbors; }
}
