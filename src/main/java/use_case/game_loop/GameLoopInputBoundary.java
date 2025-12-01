package use_case.game_loop;

/**
 * GameLoopInputBoundary defines the input port for game loop update operations.
 *
 * This interface follows Clean Architecture principles by abstracting the game
 * update logic, allowing the view layer to trigger updates without knowing
 * the implementation details.
 */
public interface GameLoopInputBoundary {

    /**
     * Performs a single game loop update tick.
     * This orchestrates movement updates, zone transitions, and proximity checks.
     *
     * @param deltaTime the time elapsed since the last update, in seconds
     */
    void update(double deltaTime);

    /**
     * Checks if a zone transition occurred during the last update.
     *
     * @return true if a zone transition occurred
     */
    boolean didZoneTransitionOccur();
}
