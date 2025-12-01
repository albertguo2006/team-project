package use_case.game_loop;

import entity.Player;
import use_case.movement.PlayerMovementInputBoundary;
import use_case.proximity.ProximityDetectionInputBoundary;
import use_case.zone_transition.ZoneTransitionInputBoundary;

/**
 * GameLoopInteractor orchestrates the game loop update logic.
 *
 * This use case follows Clean Architecture principles by coordinating
 * multiple use cases (movement, zone transition, proximity) without
 * containing any framework-specific code.
 */
public class GameLoopInteractor implements GameLoopInputBoundary {

    private static final double PLAYER_WIDTH = 64.0;
    private static final double PLAYER_HEIGHT = 64.0;

    private final PlayerMovementInputBoundary movementUseCase;
    private final ZoneTransitionInputBoundary zoneTransitionUseCase;
    private final ProximityDetectionInputBoundary proximityDetectionUseCase;

    private boolean zoneTransitionOccurred;

    /**
     * Constructs a GameLoopInteractor with the required use cases.
     *
     * @param movementUseCase the player movement use case
     * @param zoneTransitionUseCase the zone transition use case
     * @param proximityDetectionUseCase the proximity detection use case
     */
    public GameLoopInteractor(PlayerMovementInputBoundary movementUseCase,
                              ZoneTransitionInputBoundary zoneTransitionUseCase,
                              ProximityDetectionInputBoundary proximityDetectionUseCase) {
        this.movementUseCase = movementUseCase;
        this.zoneTransitionUseCase = zoneTransitionUseCase;
        this.proximityDetectionUseCase = proximityDetectionUseCase;
    }

    @Override
    public void update(double deltaTime) {
        // 1. Update player position based on input
        movementUseCase.updatePosition(deltaTime);

        // 2. Get current player position
        Player player = movementUseCase.getPlayer();
        double playerX = player.getX();
        double playerY = player.getY();

        // 3. Check for zone transitions
        zoneTransitionOccurred = zoneTransitionUseCase.checkAndPerformTransition(
                playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

        // 4. Check all proximity conditions
        String currentZoneName = zoneTransitionUseCase.getCurrentZoneName();
        proximityDetectionUseCase.checkAllProximities(playerX, playerY, currentZoneName);
    }

    @Override
    public boolean didZoneTransitionOccur() {
        return zoneTransitionOccurred;
    }
}
