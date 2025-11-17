package use_case.sleep;

import entity.Player;
import use_case.save_progress.SaveProgressInteractor;

/**
 * the Sleep Interactor
 */

public class SleepInteractor {

    private final Player player;
    private final SaveProgressInteractor saveProgressInteractor;
    private int currentDay;

    public SleepInteractor(Player player, SaveProgressInteractor saveProgressInteractor){
        this.player = player;
        this.saveProgressInteractor = saveProgressInteractor;
        this.currentDay = 1;
    }

    /**
     * Execute the sleep sequence: save game and advance to next day
     * @throws IllegalStateException if player is not in house
     */
    public void execute(){
        if (!player.isInHouse())
    }

}
