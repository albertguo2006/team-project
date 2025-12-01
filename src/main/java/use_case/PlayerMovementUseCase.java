package use_case;

import entity.Player;
import use_case.movement.PlayerMovementInputBoundary;

/**
 * PlayerMovementUseCase handles the business logic for player character movement.
 *
 * Responsibilities:
 * - Maintains the state of which directions the player is currently moving
 * - Updates the player's position based on elapsed time and movement velocity
 * - Performs collision detection and boundary checking
 * - Completely decoupled from Swing and GUI rendering
 *
 * This use case follows Clean Architecture principles by being independent of
 * any framework or presentation layer concerns. It implements PlayerMovementInputBoundary
 * to allow interface adapters to depend on an abstraction rather than this concrete class.
 */
public class PlayerMovementUseCase extends Player implements PlayerMovementInputBoundary {
    
    private final Player player;
    
    // World bounds for collision detection (virtual resolution 1920x1200)
    private static final double WORLD_WIDTH = 1920.0;
    private static final double WORLD_HEIGHT = 1200.0;
    
    // Player sprite dimensions (for more accurate collision detection)
    private static final double PLAYER_WIDTH = 64.0;
    private static final double PLAYER_HEIGHT = 64.0;
    
    // Movement state flags: which directions are currently active
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    
    /**
     * Constructs a PlayerMovementUseCase with the given player entity.
     * 
     * @param player the Player entity to manage
     */
    public PlayerMovementUseCase(Player player) {
        this.player = player;
    }
    
    /**
     * Sets or clears a movement state for a given direction.
     * This is called by PlayerInputController whenever a key is pressed or released.
     *
     * @param direction the Direction to update
     * @param isMoving true to start moving in that direction, false to stop
     */
    @Override
    public void setMovementState(Direction direction, boolean isMoving) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        if (direction == Direction.UP) {
            movingUp = isMoving;
        } else if (direction == Direction.DOWN) {
            movingDown = isMoving;
        } else if (direction == Direction.LEFT) {
            movingLeft = isMoving;
        } else if (direction == Direction.RIGHT) {
            movingRight = isMoving;
        } else {
            throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }
    
    /**
     * Updates the player's position based on current movement state and elapsed time.
     * Called once per game loop tick (approximately every 16ms at 60 FPS).
     *
     * This method:
     * 1. Calculates velocity based on active movement directions
     * 2. Computes new position: newPos = currentPos + (velocity * deltaTime)
     * 3. Checks for collisions and boundary violations (but allows edge crossing for zone transitions)
     * 4. Updates the player's x and y coordinates
     *
     * @param deltaTime the time elapsed since the last update, in seconds
     */
    @Override
    public void updatePosition(double deltaTime) {
        // Calculate velocity components based on movement state
        double velocityX = 0.0;
        double velocityY = 0.0;
        
        if (movingRight) {
            velocityX += player.getSpeed();
        }
        if (movingLeft) {
            velocityX -= player.getSpeed();
        }
        if (movingDown) {
            velocityY += player.getSpeed();
        }
        if (movingUp) {
            velocityY -= player.getSpeed();
        }
        
        // Calculate desired new position
        double newX = player.getX() + (velocityX * deltaTime);
        double newY = player.getY() + (velocityY * deltaTime);
        
        // Clamp position to stay within visible screen bounds
        // This keeps the player sprite completely visible and prevents jagged motion
        // GamePanel will trigger zone transitions when player reaches these edges
        newX = clampPosition(newX, PLAYER_WIDTH, WORLD_WIDTH);
        newY = clampPosition(newY, PLAYER_HEIGHT, WORLD_HEIGHT);
        
        // Update player position
        player.setX(newX);
        player.setY(newY);
    }
    
    /**
     * Clamps a position coordinate to ensure it stays within valid bounds.
     * 
     * @param position the position to clamp
     * @param spriteSize the size of the sprite (width or height)
     * @param worldBound the maximum world coordinate
     * @return the clamped position
     */
    private double clampPosition(double position, double spriteSize, double worldBound) {
        // Minimum bound: 0
        if (position < 0) {
            return 0;
        }
        // Maximum bound: world boundary minus sprite size
        if (position > worldBound - spriteSize) {
            return worldBound - spriteSize;
        }
        return position;
    }
    
    /**
     * Returns the player entity managed by this use case.
     * Needed by the presentation layer for rendering and other operations.
     *
     * @return the Player entity
     */
    @Override
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Returns whether the player is currently moving in any direction.
     * Useful for animation state management.
     *
     * @return true if any movement direction is active
     */
    @Override
    public boolean isMoving() {
        return movingUp || movingDown || movingLeft || movingRight;
    }
    
    /**
     * Gets the current movement direction (for animation purposes).
     * If multiple keys are pressed, returns the primary active direction.
     *
     * @return the primary active Direction, or null if not moving
     */
    @Override
    public Direction getCurrentDirection() {
        // Prioritize: Down > Up > Right > Left (arbitrary but consistent)
        if (movingDown) return Direction.DOWN;
        if (movingUp) return Direction.UP;
        if (movingRight) return Direction.RIGHT;
        if (movingLeft) return Direction.LEFT;
        return null;
    }

    /**
     * Stops all movement in all directions.
     */
    @Override
    public void stopMovement() {
        movingUp = false;
        movingDown = false;
        movingLeft = false;
        movingRight = false;
    }
}
