package use_case.movement;

import entity.Player;
import use_case.Direction;

/**
 * PlayerMovementInputBoundary defines the input port for player movement operations.
 *
 * This interface follows Clean Architecture's Dependency Inversion Principle:
 * - Interface adapters (controllers) depend on this abstraction
 * - The concrete implementation (PlayerMovementInteractor) implements this interface
 * - High-level modules do not depend on low-level modules; both depend on abstractions
 *
 * This allows the presentation layer to be decoupled from the concrete use case
 * implementation, making the system more testable and maintainable.
 */
public interface PlayerMovementInputBoundary {

    /**
     * Sets or clears a movement state for a given direction.
     * Called when a movement key is pressed or released.
     *
     * @param direction the Direction to update
     * @param isMoving true to start moving in that direction, false to stop
     */
    void setMovementState(Direction direction, boolean isMoving);

    /**
     * Updates the player's position based on current movement state and elapsed time.
     * Called once per game loop tick.
     *
     * @param deltaTime the time elapsed since the last update, in seconds
     */
    void updatePosition(double deltaTime);

    /**
     * Stops all movement in all directions.
     */
    void stopMovement();

    /**
     * Returns whether the player is currently moving in any direction.
     *
     * @return true if any movement direction is active
     */
    boolean isMoving();

    /**
     * Gets the current movement direction (for animation purposes).
     *
     * @return the primary active Direction, or null if not moving
     */
    Direction getCurrentDirection();

    /**
     * Returns the player entity managed by this use case.
     * Note: In a stricter Clean Architecture implementation, this would return
     * a DTO instead of the entity directly. Kept for backward compatibility.
     *
     * @return the Player entity
     */
    Player getPlayer();
}
