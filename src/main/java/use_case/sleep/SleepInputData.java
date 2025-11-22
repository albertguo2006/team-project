package use_case.sleep;

import entity.Player;

/**
 * Input data for the sleep use case.
 * Contains the player who wants to sleep.
 */
public class SleepInputData {
    private final Player player;
    
    /**
     * Constructs SleepInputData with the player.
     * 
     * @param player the player attempting to sleep
     */
    public SleepInputData(Player player) {
        this.player = player;
    }
    
    /**
     * Gets the player.
     * @return the Player
     */
    public Player getPlayer() {
        return player;
    }
}
