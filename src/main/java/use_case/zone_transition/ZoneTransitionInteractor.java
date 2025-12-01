package use_case.zone_transition;

import entity.GameMap;
import entity.Player;
import entity.Transition;
import entity.Zone;

/**
 * ZoneTransitionInteractor handles the business logic for zone transitions.
 *
 * This use case was extracted from GamePanel to follow Clean Architecture principles.
 * It contains all the logic for determining when and how zone transitions occur.
 */
public class ZoneTransitionInteractor implements ZoneTransitionInputBoundary {

    private static final double VIRTUAL_WIDTH = 1920.0;
    private static final double VIRTUAL_HEIGHT = 1200.0;
    private static final int SUBWAY_TRANSITION_ZONE = 300;
    private static final int SUBWAY_SPAWN_OFFSET = 50;
    private static final int EDGE_OFFSET = 5;

    private final GameMap gameMap;
    private final Player player;
    private final ZoneTransitionOutputBoundary outputBoundary;

    /**
     * Constructs a ZoneTransitionInteractor.
     *
     * @param gameMap the game map containing zones
     * @param player the player entity
     * @param outputBoundary the output boundary for presenting transition results
     */
    public ZoneTransitionInteractor(GameMap gameMap, Player player,
                                    ZoneTransitionOutputBoundary outputBoundary) {
        this.gameMap = gameMap;
        this.player = player;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public boolean checkAndPerformTransition(double playerX, double playerY,
                                              double playerWidth, double playerHeight) {
        Zone currentZone = gameMap.getCurrentZone();
        if (currentZone == null) {
            return false;
        }

        String currentZoneName = currentZone.getName();
        boolean transitioned = false;

        // Special handling for subway station transitions (300-pixel trigger zones)
        if ("Subway Station 1".equals(currentZoneName)) {
            if (playerY >= VIRTUAL_HEIGHT - SUBWAY_TRANSITION_ZONE) {
                gameMap.setCurrentZone("Subway Station 2");
                player.setY(SUBWAY_TRANSITION_ZONE + SUBWAY_SPAWN_OFFSET);
                transitioned = true;
            }
        } else if ("Subway Station 2".equals(currentZoneName)) {
            if (playerY <= SUBWAY_TRANSITION_ZONE) {
                gameMap.setCurrentZone("Subway Station 1");
                player.setY(VIRTUAL_HEIGHT - SUBWAY_TRANSITION_ZONE - SUBWAY_SPAWN_OFFSET);
                transitioned = true;
            }
        }

        // If no subway transition occurred, check normal edge-based transitions
        if (!transitioned) {
            Zone.Edge edgeReached = determineEdgeReached(playerX, playerY, playerWidth, playerHeight);

            if (edgeReached != null) {
                // First check for special transitions (tunnels, elevators, etc.)
                Transition specialTransition = gameMap.getSpecialTransition(edgeReached);
                if (specialTransition != null) {
                    gameMap.setCurrentZone(specialTransition.getToZone());
                    repositionPlayerFromTransition(specialTransition.getToEdge(), playerWidth, playerHeight);
                    transitioned = true;
                } else {
                    // Fall back to normal zone connection
                    String nextZone = currentZone.getNeighbor(edgeReached);
                    if (nextZone != null) {
                        gameMap.setCurrentZone(nextZone);
                        repositionPlayerFromEdge(edgeReached, playerWidth, playerHeight);
                        transitioned = true;
                    }
                }
            }
        }

        // Notify presenter if zone changed
        if (transitioned) {
            Zone newZone = gameMap.getCurrentZone();
            if (newZone != null) {
                ZoneTransitionOutputData outputData = new ZoneTransitionOutputData(
                        newZone.getName(),
                        newZone.getBackgroundColor(),
                        newZone.getBackgroundMusicPath()
                );
                outputBoundary.presentZoneTransition(outputData);
            }
        }

        return transitioned;
    }

    @Override
    public String getCurrentZoneName() {
        Zone currentZone = gameMap.getCurrentZone();
        return currentZone != null ? currentZone.getName() : null;
    }

    /**
     * Determines which edge of the screen the player has reached, if any.
     */
    private Zone.Edge determineEdgeReached(double playerX, double playerY,
                                           double playerWidth, double playerHeight) {
        if (playerY >= VIRTUAL_HEIGHT - playerHeight) {
            return Zone.Edge.DOWN;
        } else if (playerY <= 0) {
            return Zone.Edge.UP;
        } else if (playerX >= VIRTUAL_WIDTH - playerWidth) {
            return Zone.Edge.RIGHT;
        } else if (playerX <= 0) {
            return Zone.Edge.LEFT;
        }
        return null;
    }

    /**
     * Repositions the player when entering a zone via a normal edge connection.
     */
    private void repositionPlayerFromEdge(Zone.Edge edgeExited, double playerWidth, double playerHeight) {
        switch (edgeExited) {
            case DOWN:
                player.setY(EDGE_OFFSET);
                break;
            case UP:
                player.setY(VIRTUAL_HEIGHT - playerHeight - EDGE_OFFSET);
                break;
            case RIGHT:
                player.setX(EDGE_OFFSET);
                break;
            case LEFT:
                player.setX(VIRTUAL_WIDTH - playerWidth - EDGE_OFFSET);
                break;
        }
    }

    /**
     * Repositions the player when entering a zone via a special transition.
     */
    private void repositionPlayerFromTransition(Zone.Edge toEdge, double playerWidth, double playerHeight) {
        switch (toEdge) {
            case DOWN:
                player.setY(EDGE_OFFSET);
                break;
            case UP:
                player.setY(VIRTUAL_HEIGHT - playerHeight - EDGE_OFFSET);
                break;
            case RIGHT:
                player.setX(EDGE_OFFSET);
                break;
            case LEFT:
                player.setX(VIRTUAL_WIDTH - playerWidth - EDGE_OFFSET);
                break;
        }
    }
}
