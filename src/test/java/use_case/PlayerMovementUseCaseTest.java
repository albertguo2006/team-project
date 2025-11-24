package use_case;

import entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerMovementUseCaseTest {

    private Player player;
    private PlayerMovementUseCase movementUseCase;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer");
        player.setX(960.0); // Center of 1920 width
        player.setY(600.0); // Center of 1200 height
        player.setSpeed(200.0); // 200 pixels per second
        movementUseCase = new PlayerMovementUseCase(player);
    }

    @Test
    void testSetMovementStateUp() {
        movementUseCase.setMovementState(Direction.UP, true);
        assertTrue(movementUseCase.isMoving());
        assertEquals(Direction.UP, movementUseCase.getCurrentDirection());
    }

    @Test
    void testSetMovementStateDown() {
        movementUseCase.setMovementState(Direction.DOWN, true);
        assertTrue(movementUseCase.isMoving());
        assertEquals(Direction.DOWN, movementUseCase.getCurrentDirection());
    }

    @Test
    void testSetMovementStateLeft() {
        movementUseCase.setMovementState(Direction.LEFT, true);
        assertTrue(movementUseCase.isMoving());
        assertEquals(Direction.LEFT, movementUseCase.getCurrentDirection());
    }

    @Test
    void testSetMovementStateRight() {
        movementUseCase.setMovementState(Direction.RIGHT, true);
        assertTrue(movementUseCase.isMoving());
        assertEquals(Direction.RIGHT, movementUseCase.getCurrentDirection());
    }

    @Test
    void testUpdatePositionMovingRight() {
        movementUseCase.setMovementState(Direction.RIGHT, true);
        double initialX = player.getX();

        movementUseCase.updatePosition(0.1); // 0.1 seconds

        // Expected: initialX + (200 * 0.1) = initialX + 20
        assertEquals(initialX + 20.0, player.getX(), 0.01);
        assertEquals(600.0, player.getY(), 0.01); // Y unchanged
    }

    @Test
    void testUpdatePositionMovingLeft() {
        movementUseCase.setMovementState(Direction.LEFT, true);
        double initialX = player.getX();

        movementUseCase.updatePosition(0.1); // 0.1 seconds

        // Expected: initialX - (200 * 0.1) = initialX - 20
        assertEquals(initialX - 20.0, player.getX(), 0.01);
        assertEquals(600.0, player.getY(), 0.01); // Y unchanged
    }

    @Test
    void testUpdatePositionMovingUp() {
        movementUseCase.setMovementState(Direction.UP, true);
        double initialY = player.getY();

        movementUseCase.updatePosition(0.1); // 0.1 seconds

        // Expected: initialY - (200 * 0.1) = initialY - 20
        assertEquals(960.0, player.getX(), 0.01); // X unchanged
        assertEquals(initialY - 20.0, player.getY(), 0.01);
    }

    @Test
    void testUpdatePositionMovingDown() {
        movementUseCase.setMovementState(Direction.DOWN, true);
        double initialY = player.getY();

        movementUseCase.updatePosition(0.1); // 0.1 seconds

        // Expected: initialY + (200 * 0.1) = initialY + 20
        assertEquals(960.0, player.getX(), 0.01); // X unchanged
        assertEquals(initialY + 20.0, player.getY(), 0.01);
    }

    @Test
    void testUpdatePositionDiagonalMovement() {
        movementUseCase.setMovementState(Direction.RIGHT, true);
        movementUseCase.setMovementState(Direction.DOWN, true);
        double initialX = player.getX();
        double initialY = player.getY();

        movementUseCase.updatePosition(0.1); // 0.1 seconds

        // Both X and Y should change
        assertEquals(initialX + 20.0, player.getX(), 0.01);
        assertEquals(initialY + 20.0, player.getY(), 0.01);
    }

    @Test
    void testBoundaryClampingLeft() {
        player.setX(10.0); // Near left edge
        movementUseCase.setMovementState(Direction.LEFT, true);

        movementUseCase.updatePosition(1.0); // 1 second - should exceed boundary

        // Should be clamped to 0
        assertEquals(0.0, player.getX(), 0.01);
    }

    @Test
    void testBoundaryClampingRight() {
        player.setX(1900.0); // Near right edge (1920 - 64 = 1856 is max)
        movementUseCase.setMovementState(Direction.RIGHT, true);

        movementUseCase.updatePosition(1.0); // 1 second - should exceed boundary

        // Should be clamped to 1856 (1920 - 64)
        assertEquals(1856.0, player.getX(), 0.01);
    }

    @Test
    void testBoundaryClampingTop() {
        player.setY(10.0); // Near top edge
        movementUseCase.setMovementState(Direction.UP, true);

        movementUseCase.updatePosition(1.0); // 1 second - should exceed boundary

        // Should be clamped to 0
        assertEquals(0.0, player.getY(), 0.01);
    }

    @Test
    void testBoundaryClampingBottom() {
        player.setY(1180.0); // Near bottom edge (1200 - 64 = 1136 is max)
        movementUseCase.setMovementState(Direction.DOWN, true);

        movementUseCase.updatePosition(1.0); // 1 second - should exceed boundary

        // Should be clamped to 1136 (1200 - 64)
        assertEquals(1136.0, player.getY(), 0.01);
    }

    @Test
    void testStopMovement() {
        movementUseCase.setMovementState(Direction.RIGHT, true);
        assertTrue(movementUseCase.isMoving());

        movementUseCase.setMovementState(Direction.RIGHT, false);
        assertFalse(movementUseCase.isMoving());
        assertNull(movementUseCase.getCurrentDirection());
    }

    @Test
    void testMultipleDirectionsStopOne() {
        movementUseCase.setMovementState(Direction.RIGHT, true);
        movementUseCase.setMovementState(Direction.DOWN, true);
        assertTrue(movementUseCase.isMoving());

        movementUseCase.setMovementState(Direction.RIGHT, false);
        assertTrue(movementUseCase.isMoving()); // Still moving down
        assertEquals(Direction.DOWN, movementUseCase.getCurrentDirection());
    }

    @Test
    void testNoMovementWhenNotMoving() {
        double initialX = player.getX();
        double initialY = player.getY();

        movementUseCase.updatePosition(0.1); // 0.1 seconds

        // Position should not change
        assertEquals(initialX, player.getX(), 0.01);
        assertEquals(initialY, player.getY(), 0.01);
    }

    @Test
    void testGetPlayer() {
        Player retrievedPlayer = movementUseCase.getPlayer();
        assertSame(player, retrievedPlayer);
    }

    @Test
    void testDirectionPriority() {
        // Test that direction priority is: Down > Up > Right > Left
        movementUseCase.setMovementState(Direction.LEFT, true);
        movementUseCase.setMovementState(Direction.RIGHT, true);
        movementUseCase.setMovementState(Direction.UP, true);
        movementUseCase.setMovementState(Direction.DOWN, true);

        assertEquals(Direction.DOWN, movementUseCase.getCurrentDirection());

        movementUseCase.setMovementState(Direction.DOWN, false);
        assertEquals(Direction.UP, movementUseCase.getCurrentDirection());

        movementUseCase.setMovementState(Direction.UP, false);
        assertEquals(Direction.RIGHT, movementUseCase.getCurrentDirection());

        movementUseCase.setMovementState(Direction.RIGHT, false);
        assertEquals(Direction.LEFT, movementUseCase.getCurrentDirection());
    }

    @Test
    void testOpposingDirectionsCancelOut() {
        // Moving right and left at the same time
        movementUseCase.setMovementState(Direction.RIGHT, true);
        movementUseCase.setMovementState(Direction.LEFT, true);
        double initialX = player.getX();

        movementUseCase.updatePosition(0.1);

        // X should not change (movements cancel out)
        assertEquals(initialX, player.getX(), 0.01);

        // Same for up and down
        movementUseCase.setMovementState(Direction.RIGHT, false);
        movementUseCase.setMovementState(Direction.LEFT, false);
        movementUseCase.setMovementState(Direction.UP, true);
        movementUseCase.setMovementState(Direction.DOWN, true);
        double initialY = player.getY();

        movementUseCase.updatePosition(0.1);

        // Y should not change (movements cancel out)
        assertEquals(initialY, player.getY(), 0.01);
    }
}
