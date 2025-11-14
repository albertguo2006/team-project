package entity;

/**
 * Represents a special transition between two zones that bypass normal edge-based connections.
 * Useful for implementing tunnels, elevators, fast travel, etc.
 * 
 * Example: Subway tunnel connecting Subway Station 1 (top) to Subway Station 2 (bottom)
 */
public class Transition {
    private final String fromZone;
    private final Zone.Edge fromEdge;
    private final String toZone;
    private final Zone.Edge toEdge;
    private final String transitionName;

    /**
     * Creates a special transition between two zones.
     * 
     * @param fromZone the zone to transition from
     * @param fromEdge the edge of fromZone to trigger the transition
     * @param toZone the zone to transition to
     * @param toEdge the edge of toZone to enter from
     * @param transitionName descriptive name (e.g., "Subway Tunnel", "Elevator")
     */
    public Transition(String fromZone, Zone.Edge fromEdge, 
                      String toZone, Zone.Edge toEdge, 
                      String transitionName) {
        this.fromZone = fromZone;
        this.fromEdge = fromEdge;
        this.toZone = toZone;
        this.toEdge = toEdge;
        this.transitionName = transitionName;
    }

    public String getFromZone() { return fromZone; }
    public Zone.Edge getFromEdge() { return fromEdge; }
    public String getToZone() { return toZone; }
    public Zone.Edge getToEdge() { return toEdge; }
    public String getTransitionName() { return transitionName; }
}
