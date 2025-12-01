package use_case.zone_transition;

/**
 * ZoneTransitionInputBoundary defines the input port for zone transition operations.
 *
 * This interface follows Clean Architecture's Dependency Inversion Principle,
 * allowing the view layer to delegate zone transition logic to the use case layer
 * without depending on concrete implementations.
 */
public interface ZoneTransitionInputBoundary {

    /**
     * Checks if a zone transition should occur based on player position
     * and performs the transition if needed.
     *
     * @param playerX the player's current X position
     * @param playerY the player's current Y position
     * @param playerWidth the player's sprite width
     * @param playerHeight the player's sprite height
     * @return true if a zone transition occurred, false otherwise
     */
    boolean checkAndPerformTransition(double playerX, double playerY,
                                      double playerWidth, double playerHeight);

    /**
     * Gets the name of the current zone.
     *
     * @return the current zone name
     */
    String getCurrentZoneName();
}
