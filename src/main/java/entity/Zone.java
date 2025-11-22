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
    private final String backgroundMusicPath;
    private final Map<Edge, String> neighbors = new HashMap<>();

    public Zone(String name, Color backgroundColor, String backgroundImagePath, String backgroundMusicPath) {
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.backgroundImagePath = backgroundImagePath;
        this.backgroundMusicPath = backgroundMusicPath;
    }

    public String getName() { return name; }
    public Color getBackgroundColor() { return backgroundColor; }
    public String getBackgroundImagePath() { return backgroundImagePath; }
    public String getBackgroundMusicPath() { return backgroundMusicPath; }

    public void setNeighbor(Edge edge, String neighborZoneName) {
        neighbors.put(edge, neighborZoneName);
    }
    public String getNeighbor(Edge edge) {
        return neighbors.get(edge);
    }
    public Map<Edge, String> getNeighbors() { return neighbors; }
}
